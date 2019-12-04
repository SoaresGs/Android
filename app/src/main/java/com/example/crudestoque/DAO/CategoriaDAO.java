package com.example.crudestoque.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.crudestoque.DBhelper.DBhelper;
import com.example.crudestoque.Model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO{

    private SQLiteDatabase salvar;
    private SQLiteDatabase ler;

    public CategoriaDAO(Context context) {
        DBhelper db = new DBhelper(context);

        salvar = db.getWritableDatabase();
        ler = db.getReadableDatabase();
    }

    /*meotodo para salvar*/
    public boolean SalvarCategoria(Categoria categoria) {
        ContentValues cv = new ContentValues();
        cv.put("nome_categoria", categoria.getNomeCategoria());

        try{
            salvar.insert(DBhelper.TABELA_CATEGORIA,null, cv);
            Log.e("INFO","Salvo!");
        }catch (Exception e){
            Log.e("INFO","Erro!"+e.getMessage());
            return false;
        }
        return true;
    }


    /**/
    public List<Categoria> ListaCategoria() {
        List<Categoria> listCategoria  = new ArrayList<>();
        String sql = "SELECT * FROM "+DBhelper.TABELA_CATEGORIA+" ;";
        Cursor c = ler.rawQuery(sql,null);

        while (c.moveToNext()){
            Categoria categoria = new Categoria();

            int id = c.getInt(c.getColumnIndex("id_categoria"));
            String nome = c.getString(c.getColumnIndex("nome_categoria"));

            categoria.setIdCategoria(id);
            categoria.setNomeCategoria(nome);

            listCategoria.add(categoria);
        }
        return listCategoria;
    }
}
