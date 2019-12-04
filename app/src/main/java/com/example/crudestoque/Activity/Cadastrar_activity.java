package com.example.crudestoque.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.crudestoque.DAO.CategoriaDAO;
import com.example.crudestoque.DAO.ProdutoDAO;
import com.example.crudestoque.Model.Categoria;
import com.example.crudestoque.Model.Produto;
import com.example.crudestoque.R;

import java.util.ArrayList;
import java.util.List;

public class Cadastrar_activity extends AppCompatActivity {
    List<Categoria> listaCategoria;
    Produto produtoEditar = new Produto();

    Button salvar;
    EditText nome;
    EditText preco;
    EditText quantidade;
    EditText custo;
    Spinner spinnerCategoria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        /*Referenciando os variaveis com o XML*/
        nome = findViewById(R.id.editTextNome);
        preco = findViewById(R.id.editTextPreco);
        quantidade = findViewById(R.id.editTextQuantidade);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        custo = findViewById(R.id.editTextCusto);
        salvar = findViewById(R.id.buttonSalvar);


        /* METODO PARA MOSTRAR CATEGORIAS */
        MostrandoCategorias();

        produtoEditar = (Produto) getIntent().getSerializableExtra("produtoSelecionado");


        /* PREEMCHENDO CAMPOS COM VALORES PARA EDITAR */
        if(produtoEditar != null){
            nome.setText(produtoEditar.getNomeProduto());
            preco.setText(String.valueOf(produtoEditar.getPrecoProduto()));
            quantidade.setText(String.valueOf(produtoEditar.getQuantidadeProduto()));
            custo.setText(String.valueOf(produtoEditar.getCustoProduto()));


            //spinnerCategoria.setSelection(produtoEditar.getCategoria());
            for (int i =0;i<=listaCategoria.size()-1;i++){
                if(listaCategoria.equals(produtoEditar.getCategoria())){
                    spinnerCategoria.setSelection(produtoEditar.getCategoria());
                }
            }
        }


        /* SALVANDO PRODUTO QUANDO CLICA NO BOTAO SALVAR */
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(produtoEditar == null){
                    if (nome.getText().toString().equals("") || preco.getText().toString().equals("") || custo.getText().toString().equals("") || quantidade.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(),"Preencha todos os campos",Toast.LENGTH_SHORT);
                    }else{
                        Produto produto = new Produto();
                        ProdutoDAO produtoDAO = new ProdutoDAO(getApplicationContext());
                        produto.setNomeProduto(nome.getText().toString());
                        produto.setQuantidadeProduto(Integer.parseInt(String.valueOf(quantidade.getText())));
                        produto.setCategoria(listaCategoria.get((int)spinnerCategoria.getSelectedItemId()).getIdCategoria());
                        produto.setPrecoProduto(Double.parseDouble(preco.getText().toString()));
                        produto.setCustoProduto(Double.parseDouble(custo.getText().toString()));
                        if(produtoDAO.SalvarProduto(produto) == true){
                            Toast.makeText(getApplicationContext(), "Produto salvo", Toast.LENGTH_SHORT);
                            Listar_produto_activity.atualizar = true;
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Erro ao salvar", Toast.LENGTH_SHORT);
                        }
                    }
                }else{
                    if (nome.getText().toString().equals("") || preco.getText().toString().equals("") || custo.getText().toString().equals("") || quantidade.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(),"Preencha os campos",Toast.LENGTH_SHORT);
                    }else{
                        Produto produto = new Produto();
                        ProdutoDAO produtoDAO = new ProdutoDAO(getApplicationContext());
                        produto.setIdProduto(produtoEditar.getIdProduto());
                        produto.setNomeProduto(nome.getText().toString());
                        produto.setQuantidadeProduto(Integer.parseInt(quantidade.getText().toString()));
                        produto.setCustoProduto(Double.parseDouble(custo.getText().toString()));
                        produto.setCategoria(listaCategoria.get((int)spinnerCategoria.getSelectedItemId()).getIdCategoria());
                        produto.setPrecoProduto(Double.parseDouble(preco.getText().toString()));
                        if(produtoDAO.AtualizarProduto(produto) == true){
                            Toast.makeText(getApplicationContext(), "Produto editado", Toast.LENGTH_SHORT);
                            Listar_produto_activity.atualizar = true;
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Erro ao editar", Toast.LENGTH_SHORT);
                        }
                    }
                }
            }
        });
    }

    /* MOSTRANDO TODAS AS CATEGORIAS */
    public void MostrandoCategorias(){
        CategoriaDAO categoriaDAO = new CategoriaDAO(getApplicationContext());
        listaCategoria = categoriaDAO.ListaCategoria();

        ArrayAdapter categorias = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaCategoria);
        categorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categorias);
    }
}
