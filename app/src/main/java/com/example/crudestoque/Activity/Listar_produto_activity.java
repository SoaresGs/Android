package com.example.crudestoque.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.crudestoque.Adapter.AdapterProduto;
import com.example.crudestoque.DAO.ProdutoDAO;
import com.example.crudestoque.DBhelper.RecyclerItemClickListener;
import com.example.crudestoque.Model.Categoria;
import com.example.crudestoque.Model.Produto;
import com.example.crudestoque.R;

import java.util.ArrayList;
import java.util.List;

public class Listar_produto_activity extends AppCompatActivity {

    RecyclerView recyclerProdutos;
    Button buttonPesquisar;
    List<Produto> listProduto = new ArrayList<>();
    AdapterProduto adapterProduto;
    Categoria categoriaModel = new Categoria();
    Produto produtoSelecionado;
    EditText barraPesquisar;
    static boolean atualizar;
    ProdutoDAO produtoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_produto);

        produtoDAO = new ProdutoDAO(this);
        buttonPesquisar = findViewById(R.id.buttonPesquisar);
        barraPesquisar = findViewById(R.id.BarraPesquisarProduto);

        /* ADAPTER PRODUTOS */
        listProduto = produtoDAO.ListarProduto();
        adapterProduto = new AdapterProduto(listProduto,this);
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        recyclerProdutos.setHasFixedSize(true);
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setAdapter(adapterProduto);


        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                produtoSelecionado = listProduto.get(position);
                Intent intent = new Intent(Listar_produto_activity.this,Cadastrar_activity.class);
                intent.putExtra("produtoSelecionado", produtoSelecionado);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                produtoSelecionado = listProduto.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(Listar_produto_activity.this);
                dialog.setTitle("Confirmar Exclusão!");
                adapterProduto.remover(position);
                dialog.setMessage("Deseja realmente excluir!"+produtoSelecionado.getNomeProduto()+" ?");


                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProdutoDAO produtoDAO = new ProdutoDAO(getApplicationContext());
                        if(produtoDAO.DeletarProduto(produtoSelecionado)){

                            Toast.makeText(getApplicationContext(),"Excluido!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Erro ao tentar excluir!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("Não", null);
                dialog.create();
                dialog.show();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

        buttonPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pesquisa = barraPesquisar.getText().toString();

                adapterProduto.mudaLista(Pesquisar(pesquisa));

                if(barraPesquisar.getText().equals("")){
                    adapterProduto.mudaLista(listProduto);
                }
            }
        });

    }
    public List<Produto> Pesquisar(String pesquisa){
        ArrayList<Produto> pesquisaProduto = new ArrayList(listProduto);
        for(int i=0;i< pesquisaProduto.size();i++){
            if(! pesquisaProduto.get(i).getNomeProduto().contains(pesquisa)){
                pesquisaProduto.remove(i);
            }
        }
        return pesquisaProduto;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(atualizar){
            adapterProduto.mudaLista(produtoDAO.ListarProduto());
            atualizar = false;
        }

    }
}