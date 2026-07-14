package com.example.gestaofinanceira.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaofinanceira.Adapter.AdapterGasto;
import com.example.gestaofinanceira.DAO.GastoDAO;
import com.example.gestaofinanceira.DBhelper.RecyclerItemClickListener;
import com.example.gestaofinanceira.Model.Gasto;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.MoedaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista todos os gastos do mês selecionado. Toque longo em um item
 * permite excluí-lo.
 */
public class ListarGastosActivity extends AppCompatActivity {

    private GastoDAO dao;
    private String mes;
    private List<Gasto> lista = new ArrayList<>();
    private AdapterGasto adapter;
    private TextView textVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_gastos);

        dao = new GastoDAO(this);
        mes = getIntent().getStringExtra("mes");
        if (mes == null) {
            mes = MoedaUtil.mesAtual();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gastos • " + MoedaUtil.rotuloMes(mes));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textVazio = findViewById(R.id.textVazio);
        RecyclerView recycler = findViewById(R.id.recyclerGastos);
        adapter = new AdapterGasto(lista, this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        confirmarExclusao(position);
                    }

                    @Override
                    public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void carregar() {
        lista.clear();
        lista.addAll(dao.listarPorMes(mes));
        adapter.notifyDataSetChanged();
        textVazio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmarExclusao(final int position) {
        final Gasto gasto = lista.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Excluir gasto")
                .setMessage("Deseja excluir \"" + gasto.getDescricao() + "\"?")
                .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dao.deletar(gasto.getId())) {
                            Toast.makeText(ListarGastosActivity.this, "Gasto excluído", Toast.LENGTH_SHORT).show();
                            carregar();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
