package com.example.gestaofinanceira.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaofinanceira.DBhelper.DBhelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Acesso ao Desafio dos 100 Envelopes: cada envelope guardado corresponde a
 * poupar o valor do seu número (1 a 100), somando R$ 5.050 ao final.
 */
public class EnvelopeDAO {

    public static final int TOTAL_ENVELOPES = 100;

    private final SQLiteDatabase escreve;
    private final SQLiteDatabase le;

    public EnvelopeDAO(Context context) {
        DBhelper db = new DBhelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();
    }

    /** Marca ou desmarca um envelope. Retorna o novo estado (true = guardado). */
    public boolean alternar(int numero) {
        if (guardados().contains(numero)) {
            escreve.delete(DBhelper.TABELA_ENVELOPE, "numero = ?",
                    new String[]{String.valueOf(numero)});
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put("numero", numero);
        cv.put("data", System.currentTimeMillis());
        escreve.insertWithOnConflict(DBhelper.TABELA_ENVELOPE, null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    /** Conjunto de números de envelopes já guardados. */
    public Set<Integer> guardados() {
        Set<Integer> set = new HashSet<>();
        Cursor c = le.rawQuery("SELECT numero FROM " + DBhelper.TABELA_ENVELOPE, null);
        while (c.moveToNext()) {
            set.add(c.getInt(0));
        }
        c.close();
        return set;
    }

    /** Soma total já guardada. */
    public int totalGuardado() {
        int total = 0;
        Cursor c = le.rawQuery("SELECT SUM(numero) FROM " + DBhelper.TABELA_ENVELOPE, null);
        if (c.moveToFirst()) {
            total = c.getInt(0);
        }
        c.close();
        return total;
    }

    /** Valor total do desafio completo (1 + 2 + ... + 100 = 5050). */
    public static int metaTotal() {
        return TOTAL_ENVELOPES * (TOTAL_ENVELOPES + 1) / 2;
    }
}
