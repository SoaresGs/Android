package com.example.gestaofinanceira.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaofinanceira.DBhelper.DBhelper;
import com.example.gestaofinanceira.Model.Decisao;

import java.util.ArrayList;
import java.util.List;

/**
 * Acesso às decisões de compra do módulo "Consumo Consciente".
 */
public class DecisaoDAO {

    private final SQLiteDatabase escreve;
    private final SQLiteDatabase le;

    public DecisaoDAO(Context context) {
        DBhelper db = new DBhelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();
    }

    public long salvar(Decisao d) {
        ContentValues cv = new ContentValues();
        cv.put("descricao", d.getDescricao());
        cv.put("valor", d.getValor());
        cv.put("horas", d.getHoras());
        cv.put("status", d.getStatus());
        cv.put("decidir_em", d.getDecidirEm());
        cv.put("data", d.getData());
        try {
            return escreve.insert(DBhelper.TABELA_DECISAO, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean atualizarStatus(int id, String status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        cv.put("decidir_em", 0);
        try {
            escreve.update(DBhelper.TABELA_DECISAO, cv, "id = ?",
                    new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletar(int id) {
        try {
            escreve.delete(DBhelper.TABELA_DECISAO, "id = ?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Itens em espera, ordenados pelo prazo mais próximo. */
    public List<Decisao> listarEmEspera() {
        return consultar("status = ? ORDER BY decidir_em ASC",
                new String[]{Decisao.STATUS_ESPERANDO});
    }

    /** Decisões já finalizadas (compradas ou não), mais recentes primeiro. */
    public List<Decisao> listarHistorico() {
        return consultar("status IN (?, ?) ORDER BY data DESC",
                new String[]{Decisao.STATUS_COMPRADO, Decisao.STATUS_NAO_COMPRADO});
    }

    /** Total em dinheiro economizado (itens que decidiu não comprar). */
    public double totalEconomizado() {
        return somar("valor", Decisao.STATUS_NAO_COMPRADO);
    }

    /** Total de horas de trabalho poupadas (itens não comprados). */
    public double horasEconomizadas() {
        return somar("horas", Decisao.STATUS_NAO_COMPRADO);
    }

    private double somar(String coluna, String status) {
        double total = 0;
        Cursor c = le.rawQuery("SELECT SUM(" + coluna + ") FROM " + DBhelper.TABELA_DECISAO
                + " WHERE status = ?", new String[]{status});
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    private List<Decisao> consultar(String where, String[] args) {
        List<Decisao> lista = new ArrayList<>();
        Cursor c = le.rawQuery("SELECT * FROM " + DBhelper.TABELA_DECISAO + " WHERE " + where, args);
        while (c.moveToNext()) {
            Decisao d = new Decisao();
            d.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            d.setDescricao(c.getString(c.getColumnIndexOrThrow("descricao")));
            d.setValor(c.getDouble(c.getColumnIndexOrThrow("valor")));
            d.setHoras(c.getDouble(c.getColumnIndexOrThrow("horas")));
            d.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
            d.setDecidirEm(c.getLong(c.getColumnIndexOrThrow("decidir_em")));
            d.setData(c.getLong(c.getColumnIndexOrThrow("data")));
            lista.add(d);
        }
        c.close();
        return lista;
    }
}
