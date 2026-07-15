package com.example.gestaofinanceira.Activity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaofinanceira.Adapter.AdapterEnvelope;
import com.example.gestaofinanceira.DAO.EnvelopeDAO;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.MoedaUtil;

import java.util.Set;

/**
 * Desafio dos 100 Envelopes: uma grade em que cada número guardado equivale a
 * poupar aquele valor, somando R$ 5.050 ao completar todos.
 */
public class EnvelopesActivity extends AppCompatActivity {

    private EnvelopeDAO dao;
    private Set<Integer> guardados;
    private AdapterEnvelope adapter;

    private TextView textProgresso;
    private TextView textValor;
    private ProgressBar barra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envelopes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Desafio 100 Envelopes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dao = new EnvelopeDAO(this);
        guardados = dao.guardados();

        textProgresso = findViewById(R.id.textProgressoEnvelopes);
        textValor = findViewById(R.id.textValorEnvelopes);
        barra = findViewById(R.id.progressoEnvelopes);

        RecyclerView recycler = findViewById(R.id.recyclerEnvelopes);
        recycler.setLayoutManager(new GridLayoutManager(this, 5));
        adapter = new AdapterEnvelope(this, guardados, this::alternar);
        recycler.setAdapter(adapter);

        atualizarResumo();
    }

    private void alternar(int numero) {
        boolean agoraGuardado = dao.alternar(numero);
        if (agoraGuardado) {
            guardados.add(numero);
        } else {
            guardados.remove(numero);
        }
        adapter.notifyItemChanged(numero - 1);
        atualizarResumo();
    }

    private void atualizarResumo() {
        int qtd = guardados.size();
        int total = dao.totalGuardado();
        textProgresso.setText(qtd + " de " + EnvelopeDAO.TOTAL_ENVELOPES + " envelopes");
        textValor.setText(MoedaUtil.formatar(total) + " de " + MoedaUtil.formatar(EnvelopeDAO.metaTotal()));
        barra.setProgress(qtd);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
