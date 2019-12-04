package com.example.crudestoque.DBhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {

    public static int VERSION = 1;
    public static String NOME_Banco = "Estoque"; //nome do banco
    public static String TABELA_CATEGORIA = "categoria"; // nome da tablela categoria
    public  static String TABELA_PRODUTO = "produto"; // nome da tabela produto

    public DBhelper(@Nullable Context context) {

        super(context, NOME_Banco, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*SQL para criar a tabela de categorias*/

        String tabela_categoria = "CREATE TABLE IF NOT EXISTS "+TABELA_CATEGORIA+ " (id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, nome_categoria VARCHAR(255) NOT NULL);";

        /*SQL para criar tabela de produto*/

        String tabela_produto = "CREATE TABLE IF NOT EXISTS "+TABELA_PRODUTO+" (id_produto INTEGER PRIMARY KEY AUTOINCREMENT, nome_produto VARCHAR(255) NOT NULL, quantidade_produto int(11), preco_produto double, custo_produto double, categoria_id INTEGER NOT NULL CONSTRAINT categoria_id REFERENCES categoria (id_categoria) ON DELETE CASCADE);";
        try{
            db.execSQL(tabela_categoria);
            db.execSQL(tabela_produto);
            Log.e("INFO DB", "Sucesso ao criar a tabelas");
        }catch (Exception e){
            Log.e("INFO DB", "Erro ao criar a tabela "+e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
