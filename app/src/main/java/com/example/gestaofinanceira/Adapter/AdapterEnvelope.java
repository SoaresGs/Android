package com.example.gestaofinanceira.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaofinanceira.R;

import java.util.Set;

/**
 * Grade dos 100 envelopes. Cada célula mostra o número (valor a guardar) e
 * muda de aparência quando guardada.
 */
public class AdapterEnvelope extends RecyclerView.Adapter<AdapterEnvelope.ViewHolder> {

    public interface OnEnvelopeClick {
        void onClick(int numero);
    }

    private final Context context;
    private final Set<Integer> guardados;
    private final OnEnvelopeClick listener;

    public AdapterEnvelope(Context context, Set<Integer> guardados, OnEnvelopeClick listener) {
        this.context = context;
        this.guardados = guardados;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_envelope, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int numero = position + 1;
        holder.numero.setText(String.valueOf(numero));
        if (guardados.contains(numero)) {
            holder.numero.setBackgroundResource(R.drawable.envelope_guardado);
            holder.numero.setTextColor(Color.WHITE);
        } else {
            holder.numero.setBackgroundResource(R.drawable.envelope_aberto);
            holder.numero.setTextColor(Color.parseColor("#00695C"));
        }
        holder.numero.setOnClickListener(v -> listener.onClick(numero));
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView numero;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            numero = itemView.findViewById(R.id.textNumero);
        }
    }
}
