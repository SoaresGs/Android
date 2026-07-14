package com.example.gestaofinanceira.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaofinanceira.Model.Gasto;
import com.example.gestaofinanceira.R;
import com.example.gestaofinanceira.Util.MoedaUtil;

import java.util.List;

/**
 * Adapter que exibe cada gasto em um cartão da lista.
 */
public class AdapterGasto extends RecyclerView.Adapter<AdapterGasto.ViewHolder> {

    private final List<Gasto> lista;
    private final Context context;

    public AdapterGasto(List<Gasto> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.adapter_gasto, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gasto g = lista.get(position);
        holder.descricao.setText(g.getDescricao());
        holder.categoria.setText(g.getCategoria());
        holder.valor.setText(MoedaUtil.formatar(g.getValor()));
        holder.indicador.setBackgroundColor(corDaCategoria(g.getCategoria()));

        if (g.isParcela()) {
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setText("Parcela " + g.getParcelaAtual() + "/" + g.getParcelaTotal()
                    + "  •  restam " + g.getParcelasRestantes());
        } else {
            holder.info.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private int corDaCategoria(String categoria) {
        if (categoria == null) {
            return Color.parseColor("#607D8B");
        }
        switch (categoria) {
            case Gasto.CAT_FIXO:
                return Color.parseColor("#3F51B5");
            case Gasto.CAT_VARIAVEL:
                return Color.parseColor("#009688");
            case Gasto.CAT_MERCADO:
                return Color.parseColor("#4CAF50");
            case Gasto.CAT_FAST_FOOD:
                return Color.parseColor("#FF9800");
            case Gasto.CAT_PARCELA:
                return Color.parseColor("#E91E63");
            default:
                return Color.parseColor("#607D8B");
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView descricao;
        final TextView categoria;
        final TextView valor;
        final TextView info;
        final View indicador;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.textDescricao);
            categoria = itemView.findViewById(R.id.textCategoria);
            valor = itemView.findViewById(R.id.textValor);
            info = itemView.findViewById(R.id.textInfo);
            indicador = itemView.findViewById(R.id.viewIndicador);
        }
    }
}
