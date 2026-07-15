package com.example.gestaofinanceira.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.gestaofinanceira.DAO.GastoDAO;
import com.example.gestaofinanceira.DAO.OrcamentoDAO;
import com.example.gestaofinanceira.DAO.RendaDAO;
import com.example.gestaofinanceira.Model.Gasto;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.Exportador;
import com.example.gestaofinanceira.Util.MoedaUtil;
import com.example.gestaofinanceira.View.GraficoPizzaView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Painel principal (dashboard): total do mês, renda e saldo, gráfico de
 * distribuição, resumo por categoria com progresso do orçamento, controle
 * de parcelas e ações (definir renda, orçamento, repetir fixos, exportar).
 */
public class MainActivity extends AppCompatActivity {

    private GastoDAO dao;
    private RendaDAO rendaDAO;
    private OrcamentoDAO orcamentoDAO;
    private String mesAtual;

    private TextView textMes, textTotal, textParcelasRestantes, textRenda, textSaldo;
    private GraficoPizzaView grafico;

    private TextView valorFixo, valorVariavel, valorMercado, valorFastFood, valorParcela, valorOutros;
    private TextView metaFixo, metaVariavel, metaMercado, metaFastFood, metaParcela, metaOutros;
    private ProgressBar progFixo, progVariavel, progMercado, progFastFood, progParcela, progOutros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new GastoDAO(this);
        rendaDAO = new RendaDAO(this);
        orcamentoDAO = new OrcamentoDAO(this);
        mesAtual = MoedaUtil.mesAtual();

        textMes = findViewById(R.id.textMes);
        textTotal = findViewById(R.id.textTotalMes);
        textParcelasRestantes = findViewById(R.id.textParcelasRestantes);
        textRenda = findViewById(R.id.textRenda);
        textSaldo = findViewById(R.id.textSaldo);
        grafico = findViewById(R.id.graficoPizza);

        valorFixo = findViewById(R.id.valorFixo);
        valorVariavel = findViewById(R.id.valorVariavel);
        valorMercado = findViewById(R.id.valorMercado);
        valorFastFood = findViewById(R.id.valorFastFood);
        valorParcela = findViewById(R.id.valorParcela);
        valorOutros = findViewById(R.id.valorOutros);

        metaFixo = findViewById(R.id.metaFixo);
        metaVariavel = findViewById(R.id.metaVariavel);
        metaMercado = findViewById(R.id.metaMercado);
        metaFastFood = findViewById(R.id.metaFastFood);
        metaParcela = findViewById(R.id.metaParcela);
        metaOutros = findViewById(R.id.metaOutros);

        progFixo = findViewById(R.id.progFixo);
        progVariavel = findViewById(R.id.progVariavel);
        progMercado = findViewById(R.id.progMercado);
        progFastFood = findViewById(R.id.progFastFood);
        progParcela = findViewById(R.id.progParcela);
        progOutros = findViewById(R.id.progOutros);

        ImageButton btnAnterior = findViewById(R.id.btnMesAnterior);
        ImageButton btnProximo = findViewById(R.id.btnProximoMes);
        ImageButton btnDefinirRenda = findViewById(R.id.btnDefinirRenda);
        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionar);
        View cardVerGastos = findViewById(R.id.cardVerGastos);
        View btnOrcamento = findViewById(R.id.btnOrcamento);
        View btnRepetirFixos = findViewById(R.id.btnRepetirFixos);
        View btnExportar = findViewById(R.id.btnExportar);
        View cardValeAPena = findViewById(R.id.cardValeAPena);
        View cardDecisoes = findViewById(R.id.cardDecisoes);
        View cardEnvelopes = findViewById(R.id.cardEnvelopes);

        btnAnterior.setOnClickListener(v -> {
            mesAtual = MoedaUtil.deslocarMes(mesAtual, -1);
            atualizarPainel();
        });
        btnProximo.setOnClickListener(v -> {
            mesAtual = MoedaUtil.deslocarMes(mesAtual, 1);
            atualizarPainel();
        });
        btnDefinirRenda.setOnClickListener(v -> definirRenda());

        fabAdicionar.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AdicionarGastoActivity.class);
            i.putExtra("mes", mesAtual);
            startActivity(i);
        });
        cardVerGastos.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ListarGastosActivity.class);
            i.putExtra("mes", mesAtual);
            startActivity(i);
        });
        btnOrcamento.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, OrcamentoActivity.class)));
        btnRepetirFixos.setOnClickListener(v -> repetirFixos());
        btnExportar.setOnClickListener(v -> exportar());

        cardValeAPena.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ValeAPenaActivity.class)));
        cardDecisoes.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DecisoesActivity.class)));
        cardEnvelopes.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EnvelopesActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarPainel();
    }

    private void atualizarPainel() {
        textMes.setText(MoedaUtil.rotuloMes(mesAtual));

        double total = dao.totalDoMes(mesAtual);
        textTotal.setText(MoedaUtil.formatar(total));

        double renda = rendaDAO.getRenda(mesAtual);
        double saldo = renda - total;
        textRenda.setText(MoedaUtil.formatar(renda));
        textSaldo.setText(MoedaUtil.formatar(saldo));
        textSaldo.setTextColor(getResources().getColor(
                saldo < 0 ? R.color.catParcela : R.color.colorPrimary));

        Map<String, Double> porCat = dao.totalPorCategoria(mesAtual);
        Map<String, Double> orcamentos = orcamentoDAO.getOrcamentos();

        aplicarCategoria(Gasto.CAT_FIXO, porCat, orcamentos, valorFixo, metaFixo, progFixo);
        aplicarCategoria(Gasto.CAT_VARIAVEL, porCat, orcamentos, valorVariavel, metaVariavel, progVariavel);
        aplicarCategoria(Gasto.CAT_MERCADO, porCat, orcamentos, valorMercado, metaMercado, progMercado);
        aplicarCategoria(Gasto.CAT_FAST_FOOD, porCat, orcamentos, valorFastFood, metaFastFood, progFastFood);
        aplicarCategoria(Gasto.CAT_PARCELA, porCat, orcamentos, valorParcela, metaParcela, progParcela);
        aplicarCategoria(Gasto.CAT_OUTROS, porCat, orcamentos, valorOutros, metaOutros, progOutros);

        atualizarGrafico(porCat);

        List<Gasto> parcelas = dao.listarParcelasAtivas();
        double totalRestante = 0;
        int qtd = 0;
        for (Gasto g : parcelas) {
            totalRestante += g.getValor() * g.getParcelasRestantes();
            qtd += g.getParcelasRestantes();
        }
        textParcelasRestantes.setText(qtd + " parcelas • " + MoedaUtil.formatar(totalRestante) + " a pagar");
    }

    private void aplicarCategoria(String categoria, Map<String, Double> porCat,
                                  Map<String, Double> orcamentos, TextView valor,
                                  TextView meta, ProgressBar prog) {
        double gasto = porCat.get(categoria);
        double orcado = orcamentos.get(categoria);
        valor.setText(MoedaUtil.formatar(gasto));

        if (orcado > 0) {
            int pct = (int) Math.min(100, Math.round(gasto / orcado * 100));
            prog.setVisibility(View.VISIBLE);
            prog.setProgress(pct);
            meta.setVisibility(View.VISIBLE);
            boolean estourou = gasto > orcado;
            if (estourou) {
                meta.setText("Estourou! meta " + MoedaUtil.formatar(orcado));
                meta.setTextColor(getResources().getColor(R.color.catParcela));
            } else {
                meta.setText("de " + MoedaUtil.formatar(orcado));
                meta.setTextColor(getResources().getColor(R.color.textoSecundario));
            }
        } else {
            prog.setVisibility(View.GONE);
            meta.setVisibility(View.GONE);
        }
    }

    private void atualizarGrafico(Map<String, Double> porCat) {
        List<GraficoPizzaView.Fatia> fatias = new ArrayList<>();
        fatias.add(new GraficoPizzaView.Fatia(Gasto.CAT_FIXO, porCat.get(Gasto.CAT_FIXO), cor(R.color.catFixo)));
        fatias.add(new GraficoPizzaView.Fatia(Gasto.CAT_VARIAVEL, porCat.get(Gasto.CAT_VARIAVEL), cor(R.color.catVariavel)));
        fatias.add(new GraficoPizzaView.Fatia(Gasto.CAT_MERCADO, porCat.get(Gasto.CAT_MERCADO), cor(R.color.catMercado)));
        fatias.add(new GraficoPizzaView.Fatia(Gasto.CAT_FAST_FOOD, porCat.get(Gasto.CAT_FAST_FOOD), cor(R.color.catFastFood)));
        fatias.add(new GraficoPizzaView.Fatia(Gasto.CAT_PARCELA, porCat.get(Gasto.CAT_PARCELA), cor(R.color.catParcela)));
        fatias.add(new GraficoPizzaView.Fatia(Gasto.CAT_OUTROS, porCat.get(Gasto.CAT_OUTROS), cor(R.color.catOutros)));
        grafico.setFatias(fatias);
    }

    private int cor(int resId) {
        return getResources().getColor(resId);
    }

    private void definirRenda() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("0,00");
        double atual = rendaDAO.getRenda(mesAtual);
        if (atual > 0) {
            input.setText(String.valueOf(atual));
        }
        new AlertDialog.Builder(this)
                .setTitle("Renda de " + MoedaUtil.rotuloMes(mesAtual))
                .setMessage("Informe sua renda/salário deste mês:")
                .setView(input)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String txt = input.getText().toString().trim().replace(",", ".");
                    double valor = 0;
                    if (!txt.isEmpty()) {
                        try {
                            valor = Double.parseDouble(txt);
                        } catch (NumberFormatException e) {
                            valor = 0;
                        }
                    }
                    rendaDAO.salvarRenda(mesAtual, valor);
                    atualizarPainel();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void repetirFixos() {
        final String mesAnterior = MoedaUtil.deslocarMes(mesAtual, -1);
        new AlertDialog.Builder(this)
                .setTitle("Repetir gastos fixos")
                .setMessage("Copiar os gastos fixos de " + MoedaUtil.rotuloMes(mesAnterior)
                        + " para " + MoedaUtil.rotuloMes(mesAtual) + "?")
                .setPositiveButton("Copiar", (dialog, which) -> {
                    int copiados = dao.repetirFixos(mesAnterior, mesAtual);
                    Toast.makeText(this,
                            copiados > 0 ? copiados + " gasto(s) fixo(s) copiado(s)"
                                    : "Nenhum gasto fixo novo para copiar",
                            Toast.LENGTH_SHORT).show();
                    atualizarPainel();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void exportar() {
        List<Gasto> gastos = dao.listarPorMes(mesAtual);
        if (gastos.isEmpty()) {
            Toast.makeText(this, "Não há gastos para exportar neste mês", Toast.LENGTH_SHORT).show();
            return;
        }
        File csv = Exportador.exportarCsv(this, mesAtual, gastos);
        if (csv == null) {
            Toast.makeText(this, "Erro ao gerar o arquivo", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", csv);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Gastos " + MoedaUtil.rotuloMes(mesAtual));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Exportar gastos"));
    }
}
