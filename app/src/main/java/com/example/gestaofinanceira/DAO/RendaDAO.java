package com.example.gestaofinanceira.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaofinanceira.DBhelper.DBhelper;

/**
 * Acesso à renda mensal informada pelo usuário.
 */
public class RendaDAO {

    private final SQLiteDatabase escreve;
    private final SQLiteDatabase le;

    public RendaDAO(Context context) {
        DBhelper db = new DBhelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();
    }

    /** Retorna a renda cadastrada para o mês ou 0 se não houver. */
    public double getRenda(String mes) {
        double valor = 0;
        Cursor c = le.rawQuery(
                "SELECT valor FROM " + DBhelper.TABELA_RENDA + " WHERE mes = ?",
                new String[]{mes});
        if (c.moveToFirst()) {
            valor = c.getDouble(0);
        }
        c.close();
        return valor;
    }

    /** Salva (ou substitui) a renda do mês. */
    public boolean salvarRenda(String mes, double valor) {
        ContentValues cv = new ContentValues();
        cv.put("mes", mes);
        cv.put("valor", valor);
        try {
            escreve.insertWithOnConflict(DBhelper.TABELA_RENDA, null, cv,
                    SQLiteDatabase.CONFLICT_REPLACE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
