package com.example.gestaofinanceira.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaofinanceira.DBhelper.DBhelper;
import com.example.gestaofinanceira.Model.Gasto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Acesso ao orçamento/limite planejado por categoria.
 */
public class OrcamentoDAO {

    private final SQLiteDatabase escreve;
    private final SQLiteDatabase le;

    public OrcamentoDAO(Context context) {
        DBhelper db = new DBhelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();
    }

    /** Retorna o orçamento de todas as categorias (0 quando não definido). */
    public Map<String, Double> getOrcamentos() {
        Map<String, Double> mapa = new LinkedHashMap<>();
        for (String cat : Gasto.CATEGORIAS) {
            mapa.put(cat, 0.0);
        }
        Cursor c = le.rawQuery("SELECT categoria, valor FROM " + DBhelper.TABELA_ORCAMENTO, null);
        while (c.moveToNext()) {
            mapa.put(c.getString(0), c.getDouble(1));
        }
        c.close();
        return mapa;
    }

    /** Salva (ou substitui) o orçamento de uma categoria. Valor 0 remove o limite. */
    public boolean salvarOrcamento(String categoria, double valor) {
        try {
            if (valor <= 0) {
                escreve.delete(DBhelper.TABELA_ORCAMENTO, "categoria = ?", new String[]{categoria});
                return true;
            }
            ContentValues cv = new ContentValues();
            cv.put("categoria", categoria);
            cv.put("valor", valor);
            escreve.insertWithOnConflict(DBhelper.TABELA_ORCAMENTO, null, cv,
                    SQLiteDatabase.CONFLICT_REPLACE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Soma total planejada em todas as categorias. */
    public double getTotalOrcado() {
        double total = 0;
        Cursor c = le.rawQuery("SELECT SUM(valor) FROM " + DBhelper.TABELA_ORCAMENTO, null);
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }
}
