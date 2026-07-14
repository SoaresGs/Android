package com.example.gestaofinanceira.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaofinanceira.DAO.GastoDAO;
import com.example.gestaofinanceira.Model.Gasto;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.MoedaUtil;

/**
 * Tela de cadastro/edição de um gasto. Quando a categoria é "Parcela",
 * os campos de parcela atual/total são exibidos. Se receber o extra
 * "gasto_id", entra em modo de edição.
 */
public class AdicionarGastoActivity extends AppCompatActivity {

    private GastoDAO dao;
    private String mes;
    private Gasto emEdicao; // null quando é um novo gasto

    private EditText editDescricao;
    private EditText editValor;
    private Spinner spinnerCategoria;
    private LinearLayout areaParcela;
    private EditText editParcelaAtual;
    private EditText editParcelaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_gasto);

        dao = new GastoDAO(this);
        mes = getIntent().getStringExtra("mes");
        if (mes == null) {
            mes = MoedaUtil.mesAtual();
        }

        int gastoId = getIntent().getIntExtra("gasto_id", -1);
        if (gastoId != -1) {
            emEdicao = dao.buscarPorId(gastoId);
            if (emEdicao != null) {
                mes = emEdicao.getMes();
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(emEdicao != null ? "Editar gasto" : "Novo gasto");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        areaParcela = findViewById(R.id.areaParcela);
        editParcelaAtual = findViewById(R.id.editParcelaAtual);
        editParcelaTotal = findViewById(R.id.editParcelaTotal);
        Button btnSalvar = findViewById(R.id.btnSalvar);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Gasto.CATEGORIAS);
        spinnerCategoria.setAdapter(adapter);

        spinnerCategoria.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                boolean isParcela = Gasto.CAT_PARCELA.equals(Gasto.CATEGORIAS[position]);
                areaParcela.setVisibility(isParcela ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        if (emEdicao != null) {
            preencherCampos(emEdicao);
            btnSalvar.setText("Salvar alterações");
        }

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void preencherCampos(Gasto g) {
        editDescricao.setText(g.getDescricao());
        editValor.setText(String.valueOf(g.getValor()));
        for (int i = 0; i < Gasto.CATEGORIAS.length; i++) {
            if (Gasto.CATEGORIAS[i].equals(g.getCategoria())) {
                spinnerCategoria.setSelection(i);
                break;
            }
        }
        if (g.isParcela()) {
            editParcelaAtual.setText(String.valueOf(g.getParcelaAtual()));
            editParcelaTotal.setText(String.valueOf(g.getParcelaTotal()));
        }
    }

    private void salvar() {
        String descricao = editDescricao.getText().toString().trim();
        String valorStr = editValor.getText().toString().trim().replace(",", ".");
        String categoria = (String) spinnerCategoria.getSelectedItem();

        if (TextUtils.isEmpty(descricao)) {
            editDescricao.setError("Informe a descrição");
            return;
        }
        if (TextUtils.isEmpty(valorStr)) {
            editValor.setError("Informe o valor");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            editValor.setError("Valor inválido");
            return;
        }
        if (valor <= 0) {
            editValor.setError("O valor deve ser maior que zero");
            return;
        }

        Gasto gasto = emEdicao != null ? emEdicao : new Gasto();
        gasto.setDescricao(descricao);
        gasto.setValor(valor);
        gasto.setCategoria(categoria);
        gasto.setMes(mes);
        if (emEdicao == null) {
            gasto.setData(System.currentTimeMillis());
        }

        if (Gasto.CAT_PARCELA.equals(categoria)) {
            int atual = parseInt(editParcelaAtual.getText().toString(), 1);
            int total = parseInt(editParcelaTotal.getText().toString(), 0);
            if (total <= 0) {
                editParcelaTotal.setError("Informe o total de parcelas");
                return;
            }
            if (atual < 1) {
                atual = 1;
            }
            if (atual > total) {
                editParcelaAtual.setError("Parcela atual maior que o total");
                return;
            }
            gasto.setParcelaAtual(atual);
            gasto.setParcelaTotal(total);
        } else {
            gasto.setParcelaAtual(0);
            gasto.setParcelaTotal(0);
        }

        boolean ok = emEdicao != null ? dao.atualizar(gasto) : dao.salvar(gasto);
        if (ok) {
            Toast.makeText(this, emEdicao != null ? "Gasto atualizado!" : "Gasto salvo!",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao salvar o gasto", Toast.LENGTH_SHORT).show();
        }
    }

    private int parseInt(String texto, int padrao) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
