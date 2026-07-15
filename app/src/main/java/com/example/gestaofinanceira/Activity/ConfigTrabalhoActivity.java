package com.example.gestaofinanceira.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaofinanceira.DAO.RendaDAO;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.ConfigTrabalho;
import com.example.gestaofinanceira.Util.MoedaUtil;

/**
 * Configuração usada para converter preço em horas de trabalho: salário
 * mensal e horas trabalhadas por mês.
 */
public class ConfigTrabalhoActivity extends AppCompatActivity {

    private EditText editSalario;
    private EditText editHoras;
    private TextView textValorHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_trabalho);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meu trabalho");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editSalario = findViewById(R.id.editSalario);
        editHoras = findViewById(R.id.editHorasMes);
        textValorHora = findViewById(R.id.textValorHoraPreview);
        Button btnSalvar = findViewById(R.id.btnSalvarConfig);

        double salario = ConfigTrabalho.getSalario(this);
        double horas = ConfigTrabalho.getHorasMes(this);

        // Sugere o salário a partir da renda do mês atual, se ainda não houver.
        if (salario <= 0) {
            double renda = new RendaDAO(this).getRenda(MoedaUtil.mesAtual());
            if (renda > 0) {
                salario = renda;
            }
        }
        if (salario > 0) {
            editSalario.setText(String.valueOf(salario));
        }
        editHoras.setText(String.valueOf(horas <= 0 ? ConfigTrabalho.HORAS_MES_PADRAO : horas));

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        double salario = parse(editSalario.getText().toString());
        double horas = parse(editHoras.getText().toString());

        if (salario <= 0) {
            editSalario.setError("Informe seu salário mensal");
            return;
        }
        if (horas <= 0) {
            editHoras.setError("Informe as horas por mês");
            return;
        }

        ConfigTrabalho.salvar(this, salario, horas);
        textValorHora.setText("Sua hora vale " + MoedaUtil.formatar(salario / horas));
        Toast.makeText(this, "Configuração salva!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private double parse(String texto) {
        if (TextUtils.isEmpty(texto)) {
            return 0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
