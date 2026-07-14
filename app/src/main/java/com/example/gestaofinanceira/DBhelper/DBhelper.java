package com.example.gestaofinanceira.DBhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Helper responsável pela criação e versionamento do banco SQLite
 * do aplicativo de gestão financeira.
 */
public class DBhelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String NOME_BANCO = "GestaoFinanceira";
    public static final String TABELA_GASTO = "gasto";

    public DBhelper(@Nullable Context context) {
        super(context, NOME_BANCO, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tabelaGasto = "CREATE TABLE IF NOT EXISTS " + TABELA_GASTO + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "descricao VARCHAR(255) NOT NULL, "
                + "valor DOUBLE NOT NULL, "
                + "categoria VARCHAR(50) NOT NULL, "
                + "parcela_atual INTEGER DEFAULT 0, "
                + "parcela_total INTEGER DEFAULT 0, "
                + "mes VARCHAR(7) NOT NULL, "
                + "data LONG NOT NULL);";
        try {
            db.execSQL(tabelaGasto);
            Log.i("INFO_DB", "Tabela de gastos criada com sucesso");
        } catch (Exception e) {
            Log.e("INFO_DB", "Erro ao criar a tabela: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_GASTO);
        onCreate(db);
    }
}
