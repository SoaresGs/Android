package com.example.crudestoque.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crudestoque.Model.Produto;
import com.example.crudestoque.R;
import java.util.ArrayList;
import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    List<Produto> listProduto = new ArrayList<>();
    private Context context;

    public AdapterProduto(List<Produto> listProduto, Context context) {
        this.listProduto = listProduto;
        this.context = context;
    }
    public void  remover(int posisao){
        listProduto.remove(posisao);
        notifyItemRemoved(posisao);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto,parent,false);
        return new AdapterProduto.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = listProduto.get(position);
        holder.nome.setText("Nome - "+produto.getNomeProduto());
        holder.categoria.setText("Categoria - "+produto.getCategoria());
        holder.quantidade.setText("Quantidade - "+String.valueOf(produto.getQuantidadeProduto()));
        holder.preco.setText("Preço - "+String.valueOf(produto.getPrecoProduto()+" R$"));
        holder.custo.setText("Custo - "+String.valueOf(produto.getCustoProduto()+" R$"));
    }

    public void mudaLista(List<Produto> listProduto){
        this.listProduto = listProduto;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listProduto.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView quantidade;
        TextView preco;
        TextView categoria;
        TextView custo;
        ImageView imagem;

        public MyViewHolder(View itemView){
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNome);
            quantidade = itemView.findViewById(R.id.textViewQuantidade);
            preco =  itemView.findViewById(R.id.textViewPreco);
            categoria = itemView.findViewById(R.id.textViewCategoria);
            custo = itemView.findViewById(R.id.textViewCusto);
            imagem = itemView.findViewById(R.id.imageViewProduto);
        }
    }
}
