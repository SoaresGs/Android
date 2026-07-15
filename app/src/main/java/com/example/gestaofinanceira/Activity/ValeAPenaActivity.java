package com.example.gestaofinanceira.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaofinanceira.DAO.DecisaoDAO;
import com.example.gestaofinanceira.Model.Decisao;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.ConfigTrabalho;
import com.example.gestaofinanceira.Util.MoedaUtil;

/**
 * Calculadora de consumo consciente (inspirada no TapDin): converte o preço
 * de um item em horas de trabalho e ajuda a decidir se vale a pena comprar.
 */
public class ValeAPenaActivity extends AppCompatActivity {

    private DecisaoDAO dao;

    private EditText editDescricao;
    private EditText editPreco;
    private View cardResultado;
    private View areaDecisao;
    private TextView textTempo;
    private TextView textDetalhe;
    private TextView textFrase;
    private TextView textAvisoConfig;

    private double precoAtual;
    private double horasAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vale_a_pena);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vale a pena?");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dao = new DecisaoDAO(this);

        editDescricao = findViewById(R.id.editItemDescricao);
        editPreco = findViewById(R.id.editItemPreco);
        cardResultado = findViewById(R.id.cardResultado);
        areaDecisao = findViewById(R.id.areaDecisao);
        textTempo = findViewById(R.id.textTempo);
        textDetalhe = findViewById(R.id.textDetalhe);
        textFrase = findViewById(R.id.textFrase);
        textAvisoConfig = findViewById(R.id.textAvisoConfig);

        Button btnCalcular = findViewById(R.id.btnCalcular);
        Button btnComprei = findViewById(R.id.btnComprei);
        Button btnEsperar = findViewById(R.id.btnEsperar);
        Button btnNaoComprar = findViewById(R.id.btnNaoComprar);
        View btnConfig = findViewById(R.id.btnMeuTrabalho);
        View btnDecisoes = findViewById(R.id.btnMinhasDecisoes);

        btnCalcular.setOnClickListener(v -> calcular());
        btnComprei.setOnClickListener(v -> registrar(Decisao.STATUS_COMPRADO));
        btnNaoComprar.setOnClickListener(v -> registrar(Decisao.STATUS_NAO_COMPRADO));
        btnEsperar.setOnClickListener(v -> escolherEspera());
        btnConfig.setOnClickListener(v ->
                startActivity(new Intent(this, ConfigTrabalhoActivity.class)));
        btnDecisoes.setOnClickListener(v ->
                startActivity(new Intent(this, DecisoesActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean precisa = ConfigTrabalho.precisaConfigurar(this);
        textAvisoConfig.setVisibility(precisa ? View.VISIBLE : View.GONE);
    }

    private void calcular() {
        if (ConfigTrabalho.precisaConfigurar(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Configure seu trabalho")
                    .setMessage("Para calcular o custo em horas, informe primeiro seu salário.")
                    .setPositiveButton("Configurar", (d, w) ->
                            startActivity(new Intent(this, ConfigTrabalhoActivity.class)))
                    .setNegativeButton("Cancelar", null)
                    .show();
            return;
        }

        String precoStr = editPreco.getText().toString().trim().replace(",", ".");
        if (TextUtils.isEmpty(precoStr)) {
            editPreco.setError("Informe o preço");
            return;
        }
        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            editPreco.setError("Preço inválido");
            return;
        }
        if (preco <= 0) {
            editPreco.setError("O preço deve ser maior que zero");
            return;
        }

        precoAtual = preco;
        horasAtual = ConfigTrabalho.horasPara(this, preco);

        String tempo = MoedaUtil.tempoDeTrabalho(horasAtual, ConfigTrabalho.HORAS_POR_DIA);
        textTempo.setText(tempo + " de trabalho");
        textDetalhe.setText(MoedaUtil.formatar(preco) + "  •  sua hora vale "
                + MoedaUtil.formatar(ConfigTrabalho.valorHora(this)));
        textFrase.setText(fraseReflexao(horasAtual));

        cardResultado.setVisibility(View.VISIBLE);
        areaDecisao.setVisibility(View.VISIBLE);
    }

    private String fraseReflexao(double horas) {
        double dias = horas / ConfigTrabalho.HORAS_POR_DIA;
        if (dias >= 5) {
            return "É mais de uma semana de trabalho. Vale mesmo a pena?";
        }
        if (dias >= 1) {
            return "São dias inteiros do seu tempo. Que tal esperar antes de decidir?";
        }
        if (horas >= 3) {
            return "Uma boa parte do seu dia. Pense com calma.";
        }
        return "Custa pouco do seu tempo — mas todo gasto conta.";
    }

    private void escolherEspera() {
        final String[] opcoes = {"1 hora", "1 dia", "3 dias", "7 dias"};
        final long[] duracoes = {
                3600_000L,
                86_400_000L,
                3L * 86_400_000L,
                7L * 86_400_000L
        };
        new AlertDialog.Builder(this)
                .setTitle("Esperar antes de decidir")
                .setItems(opcoes, (dialog, which) -> {
                    long decidirEm = System.currentTimeMillis() + duracoes[which];
                    salvar(Decisao.STATUS_ESPERANDO, decidirEm);
                    Toast.makeText(this, "Guardado. Reveja em " + opcoes[which] + ".",
                            Toast.LENGTH_SHORT).show();
                    limpar();
                })
                .show();
    }

    private void registrar(String status) {
        salvar(status, 0);
        if (Decisao.STATUS_NAO_COMPRADO.equals(status)) {
            Toast.makeText(this, "Boa! Você economizou " + MoedaUtil.formatar(precoAtual) + ".",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Registrado.", Toast.LENGTH_SHORT).show();
        }
        limpar();
    }

    private void salvar(String status, long decidirEm) {
        String descricao = editDescricao.getText().toString().trim();
        if (TextUtils.isEmpty(descricao)) {
            descricao = "Compra";
        }
        Decisao d = new Decisao();
        d.setDescricao(descricao);
        d.setValor(precoAtual);
        d.setHoras(horasAtual);
        d.setStatus(status);
        d.setDecidirEm(decidirEm);
        d.setData(System.currentTimeMillis());
        dao.salvar(d);
    }

    private void limpar() {
        editDescricao.setText("");
        editPreco.setText("");
        cardResultado.setVisibility(View.GONE);
        areaDecisao.setVisibility(View.GONE);
        precoAtual = 0;
        horasAtual = 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
