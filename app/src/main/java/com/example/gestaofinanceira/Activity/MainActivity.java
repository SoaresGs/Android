package com.example.gestaofinanceira.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaofinanceira.DAO.GastoDAO;
import com.example.gestaofinanceira.Model.Gasto;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.MoedaUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

/**
 * Painel principal (dashboard): mostra o total do mês, o resumo por
 * categoria, o total ainda a pagar em parcelas e navegação entre meses.
 */
public class MainActivity extends AppCompatActivity {

    private GastoDAO dao;
    private String mesAtual;

    private TextView textMes;
    private TextView textTotal;
    private TextView textParcelasRestantes;
    private TextView valorFixo, valorVariavel, valorMercado, valorFastFood, valorParcela, valorOutros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new GastoDAO(this);
        mesAtual = MoedaUtil.mesAtual();

        textMes = findViewById(R.id.textMes);
        textTotal = findViewById(R.id.textTotalMes);
        textParcelasRestantes = findViewById(R.id.textParcelasRestantes);
        valorFixo = findViewById(R.id.valorFixo);
        valorVariavel = findViewById(R.id.valorVariavel);
        valorMercado = findViewById(R.id.valorMercado);
        valorFastFood = findViewById(R.id.valorFastFood);
        valorParcela = findViewById(R.id.valorParcela);
        valorOutros = findViewById(R.id.valorOutros);

        ImageButton btnAnterior = findViewById(R.id.btnMesAnterior);
        ImageButton btnProximo = findViewById(R.id.btnProximoMes);
        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionar);
        View cardVerGastos = findViewById(R.id.cardVerGastos);

        btnAnterior.setOnClickListener(v -> {
            mesAtual = MoedaUtil.deslocarMes(mesAtual, -1);
            atualizarPainel();
        });

        btnProximo.setOnClickListener(v -> {
            mesAtual = MoedaUtil.deslocarMes(mesAtual, 1);
            atualizarPainel();
        });

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarPainel();
    }

    private void atualizarPainel() {
        textMes.setText(MoedaUtil.rotuloMes(mesAtual));
        textTotal.setText(MoedaUtil.formatar(dao.totalDoMes(mesAtual)));

        Map<String, Double> porCat = dao.totalPorCategoria(mesAtual);
        valorFixo.setText(MoedaUtil.formatar(porCat.get(Gasto.CAT_FIXO)));
        valorVariavel.setText(MoedaUtil.formatar(porCat.get(Gasto.CAT_VARIAVEL)));
        valorMercado.setText(MoedaUtil.formatar(porCat.get(Gasto.CAT_MERCADO)));
        valorFastFood.setText(MoedaUtil.formatar(porCat.get(Gasto.CAT_FAST_FOOD)));
        valorParcela.setText(MoedaUtil.formatar(porCat.get(Gasto.CAT_PARCELA)));
        valorOutros.setText(MoedaUtil.formatar(porCat.get(Gasto.CAT_OUTROS)));

        List<Gasto> parcelas = dao.listarParcelasAtivas();
        double totalRestante = 0;
        int qtd = 0;
        for (Gasto g : parcelas) {
            totalRestante += g.getValor() * g.getParcelasRestantes();
            qtd += g.getParcelasRestantes();
        }
        textParcelasRestantes.setText(qtd + " parcelas • " + MoedaUtil.formatar(totalRestante) + " a pagar");
    }
}
