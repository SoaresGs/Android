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

    public static final int VERSION = 3;
    public static final String NOME_BANCO = "GestaoFinanceira";
    public static final String TABELA_GASTO = "gasto";
    public static final String TABELA_RENDA = "renda";
    public static final String TABELA_ORCAMENTO = "orcamento";
    public static final String TABELA_DECISAO = "decisao";
    public static final String TABELA_ENVELOPE = "envelope";

    public DBhelper(@Nullable Context context) {
        super(context, NOME_BANCO, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        criarTabelas(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cria apenas as tabelas que ainda não existem, preservando os gastos.
        criarTabelas(db);
    }

    private void criarTabelas(SQLiteDatabase db) {
        String tabelaGasto = "CREATE TABLE IF NOT EXISTS " + TABELA_GASTO + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "descricao VARCHAR(255) NOT NULL, "
                + "valor DOUBLE NOT NULL, "
                + "categoria VARCHAR(50) NOT NULL, "
                + "parcela_atual INTEGER DEFAULT 0, "
                + "parcela_total INTEGER DEFAULT 0, "
                + "mes VARCHAR(7) NOT NULL, "
                + "data LONG NOT NULL);";

        // Renda mensal informada pelo usuário (uma linha por mês).
        String tabelaRenda = "CREATE TABLE IF NOT EXISTS " + TABELA_RENDA + " ("
                + "mes VARCHAR(7) PRIMARY KEY, "
                + "valor DOUBLE NOT NULL);";

        // Orçamento/limite planejado por categoria (uma linha por categoria).
        String tabelaOrcamento = "CREATE TABLE IF NOT EXISTS " + TABELA_ORCAMENTO + " ("
                + "categoria VARCHAR(50) PRIMARY KEY, "
                + "valor DOUBLE NOT NULL);";

        // Decisões de compra do módulo "Consumo Consciente" (TapDin).
        String tabelaDecisao = "CREATE TABLE IF NOT EXISTS " + TABELA_DECISAO + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "descricao VARCHAR(255) NOT NULL, "
                + "valor DOUBLE NOT NULL, "
                + "horas DOUBLE NOT NULL, "
                + "status VARCHAR(20) NOT NULL, "
                + "decidir_em LONG DEFAULT 0, "
                + "data LONG NOT NULL);";

        // Envelopes guardados no Desafio dos 100 Envelopes.
        String tabelaEnvelope = "CREATE TABLE IF NOT EXISTS " + TABELA_ENVELOPE + " ("
                + "numero INTEGER PRIMARY KEY, "
                + "data LONG NOT NULL);";

        try {
            db.execSQL(tabelaGasto);
            db.execSQL(tabelaRenda);
            db.execSQL(tabelaOrcamento);
            db.execSQL(tabelaDecisao);
            db.execSQL(tabelaEnvelope);
            Log.i("INFO_DB", "Tabelas criadas com sucesso");
        } catch (Exception e) {
            Log.e("INFO_DB", "Erro ao criar as tabelas: " + e.getMessage());
        }
    }
}
