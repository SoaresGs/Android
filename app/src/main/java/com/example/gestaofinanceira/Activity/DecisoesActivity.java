package com.example.gestaofinanceira.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaofinanceira.DAO.DecisaoDAO;
import com.example.gestaofinanceira.Model.Decisao;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.ConfigTrabalho;
import com.example.gestaofinanceira.Util.MoedaUtil;

import java.util.List;

/**
 * Mostra o total economizado, os itens em espera e o histórico de decisões.
 */
public class DecisoesActivity extends AppCompatActivity {

    private DecisaoDAO dao;
    private LinearLayout containerEspera;
    private LinearLayout containerHistorico;
    private TextView textEconomia;
    private TextView textHorasEconomia;
    private TextView tituloEspera;
    private TextView tituloHistorico;
    private TextView textVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decisoes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Minhas decisões");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dao = new DecisaoDAO(this);
        containerEspera = findViewById(R.id.containerEspera);
        containerHistorico = findViewById(R.id.containerHistorico);
        textEconomia = findViewById(R.id.textEconomiaTotal);
        textHorasEconomia = findViewById(R.id.textHorasEconomia);
        tituloEspera = findViewById(R.id.tituloEspera);
        tituloHistorico = findViewById(R.id.tituloHistorico);
        textVazio = findViewById(R.id.textVazio);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void carregar() {
        textEconomia.setText(MoedaUtil.formatar(dao.totalEconomizado()));
        textHorasEconomia.setText(MoedaUtil.formatarHoras(dao.horasEconomizadas())
                + " do seu tempo de volta");

        List<Decisao> espera = dao.listarEmEspera();
        List<Decisao> historico = dao.listarHistorico();

        containerEspera.removeAllViews();
        containerHistorico.removeAllViews();

        for (Decisao d : espera) {
            containerEspera.addView(criarItem(d, true));
        }
        for (Decisao d : historico) {
            containerHistorico.addView(criarItem(d, false));
        }

        tituloEspera.setVisibility(espera.isEmpty() ? View.GONE : View.VISIBLE);
        containerEspera.setVisibility(espera.isEmpty() ? View.GONE : View.VISIBLE);
        tituloHistorico.setVisibility(historico.isEmpty() ? View.GONE : View.VISIBLE);
        containerHistorico.setVisibility(historico.isEmpty() ? View.GONE : View.VISIBLE);
        textVazio.setVisibility(espera.isEmpty() && historico.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private View criarItem(final Decisao d, boolean emEspera) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_decisao, containerEspera, false);
        View strip = item.findViewById(R.id.itemStrip);
        TextView desc = item.findViewById(R.id.itemDescricao);
        TextView detalhe = item.findViewById(R.id.itemDetalhe);
        TextView status = item.findViewById(R.id.itemStatus);

        desc.setText(d.getDescricao());
        detalhe.setText(MoedaUtil.formatar(d.getValor()) + "  •  "
                + MoedaUtil.tempoDeTrabalho(d.getHoras(), ConfigTrabalho.HORAS_POR_DIA));

        if (emEspera) {
            if (d.isProntaParaDecidir()) {
                strip.setBackgroundColor(Color.parseColor("#FF9800"));
                status.setText("Hora de decidir — toque para escolher");
                status.setTextColor(Color.parseColor("#E65100"));
                item.setOnClickListener(v -> decidirAgora(d));
            } else {
                strip.setBackgroundColor(Color.parseColor("#FF9800"));
                status.setText("Aguardando • " + tempoRestante(d.getDecidirEm()));
                status.setTextColor(Color.parseColor("#757575"));
                item.setOnClickListener(v -> Toast.makeText(this,
                        "Ainda no período de espera.", Toast.LENGTH_SHORT).show());
            }
        } else if (Decisao.STATUS_NAO_COMPRADO.equals(d.getStatus())) {
            strip.setBackgroundColor(Color.parseColor("#4CAF50"));
            status.setText("Economizado");
            status.setTextColor(Color.parseColor("#2E7D32"));
            item.setOnClickListener(v -> confirmarExcluir(d));
        } else {
            strip.setBackgroundColor(Color.parseColor("#607D8B"));
            status.setText("Comprado");
            status.setTextColor(Color.parseColor("#546E7A"));
            item.setOnClickListener(v -> confirmarExcluir(d));
        }
        return item;
    }

    private void decidirAgora(final Decisao d) {
        new AlertDialog.Builder(this)
                .setTitle(d.getDescricao())
                .setMessage("O prazo de espera acabou. E aí, o que você decidiu?")
                .setPositiveButton("Não comprei", (dialog, w) -> {
                    dao.atualizarStatus(d.getId(), Decisao.STATUS_NAO_COMPRADO);
                    Toast.makeText(this, "Você economizou " + MoedaUtil.formatar(d.getValor()) + "!",
                            Toast.LENGTH_LONG).show();
                    carregar();
                })
                .setNegativeButton("Comprei", (dialog, w) -> {
                    dao.atualizarStatus(d.getId(), Decisao.STATUS_COMPRADO);
                    carregar();
                })
                .show();
    }

    private void confirmarExcluir(final Decisao d) {
        new AlertDialog.Builder(this)
                .setTitle("Remover")
                .setMessage("Remover \"" + d.getDescricao() + "\" do histórico?")
                .setPositiveButton("Remover", (dialog, w) -> {
                    dao.deletar(d.getId());
                    carregar();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String tempoRestante(long decidirEm) {
        long ms = decidirEm - System.currentTimeMillis();
        if (ms <= 0) {
            return "pronto";
        }
        long horas = ms / 3600_000L;
        if (horas >= 24) {
            long dias = horas / 24;
            return "faltam " + dias + (dias == 1 ? " dia" : " dias");
        }
        if (horas >= 1) {
            return "faltam " + horas + "h";
        }
        long min = ms / 60_000L;
        return "faltam " + Math.max(1, min) + "min";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
