package com.example.gestaofinanceira.Activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaofinanceira.DAO.OrcamentoDAO;
import com.example.gestaofinanceira.Model.Gasto;
import com.example.gestaofinanceira.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Permite definir o orçamento/limite mensal de cada categoria.
 * O painel usa esses valores para alertar quando o gasto ultrapassa a meta.
 */
public class OrcamentoActivity extends AppCompatActivity {

    private OrcamentoDAO dao;
    private final Map<String, EditText> campos = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Orçamento por categoria");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dao = new OrcamentoDAO(this);
        LinearLayout container = findViewById(R.id.containerOrcamentos);
        Button btnSalvar = findViewById(R.id.btnSalvarOrcamento);

        Map<String, Double> atuais = dao.getOrcamentos();
        int padding = (int) (12 * getResources().getDisplayMetrics().density);

        for (String cat : Gasto.CATEGORIAS) {
            LinearLayout linha = new LinearLayout(this);
            linha.setOrientation(LinearLayout.HORIZONTAL);
            linha.setGravity(Gravity.CENTER_VERTICAL);
            linha.setPadding(0, padding, 0, padding);

            TextView nome = new TextView(this);
            nome.setText(cat);
            nome.setTextSize(15);
            nome.setTextColor(getResources().getColor(R.color.textoPrincipal));
            LinearLayout.LayoutParams lpNome = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            nome.setLayoutParams(lpNome);

            EditText valor = new EditText(this);
            valor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            valor.setHint("0,00");
            valor.setEms(6);
            valor.setGravity(Gravity.END);
            double v = atuais.get(cat);
            if (v > 0) {
                valor.setText(String.valueOf(v));
            }

            linha.addView(nome);
            linha.addView(valor);
            container.addView(linha);
            campos.put(cat, valor);
        }

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        for (Map.Entry<String, EditText> e : campos.entrySet()) {
            String txt = e.getValue().getText().toString().trim().replace(",", ".");
            double valor = 0;
            if (!txt.isEmpty()) {
                try {
                    valor = Double.parseDouble(txt);
                } catch (NumberFormatException ex) {
                    valor = 0;
                }
            }
            dao.salvarOrcamento(e.getKey(), valor);
        }
        Toast.makeText(this, "Orçamento salvo!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
