package com.example.crudestoque.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.crudestoque.DAO.CategoriaDAO;
import com.example.crudestoque.Model.Categoria;
import com.example.crudestoque.R;

public class MainActivity extends AppCompatActivity {
    Button abrirCadastrar,abrirListar;
    Categoria categoria = new Categoria();
    CategoriaDAO cateDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cateDao = new CategoriaDAO(this);
        SalvarCategorias();


        abrirCadastrar = findViewById(R.id.AbrirCadastrar);
        abrirListar = findViewById(R.id.AbrirListar);


        abrirCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Cadastrar_activity.class);
                startActivity(intent);
            }
        });



        abrirListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Listar_produto_activity.class);
                startActivity(intent);
            }
        });
    }

    /* SALVAR AS CATEGORIAS*/
    public void SalvarCategorias(){
        //categoria.setNomeCategoria("Verduras");
        //categoria.setNomeCategoria("Limpeza");
        //categoria.setNomeCategoria("Frios");
        cateDao.SalvarCategoria(categoria);
    }
}
