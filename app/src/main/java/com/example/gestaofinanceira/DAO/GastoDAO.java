package com.example.gestaofinanceira.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaofinanceira.DBhelper.DBhelper;
import com.example.gestaofinanceira.Model.Gasto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acesso a dados dos gastos: inserir, atualizar, excluir e consultar,
 * além de agregações usadas pelo painel (dashboard).
 */
public class GastoDAO {

    private final SQLiteDatabase escreve;
    private final SQLiteDatabase le;

    public GastoDAO(Context context) {
        DBhelper db = new DBhelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();
    }

    public boolean salvar(Gasto gasto) {
        ContentValues cv = new ContentValues();
        cv.put("descricao", gasto.getDescricao());
        cv.put("valor", gasto.getValor());
        cv.put("categoria", gasto.getCategoria());
        cv.put("parcela_atual", gasto.getParcelaAtual());
        cv.put("parcela_total", gasto.getParcelaTotal());
        cv.put("mes", gasto.getMes());
        cv.put("data", gasto.getData());
        try {
            escreve.insert(DBhelper.TABELA_GASTO, null, cv);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean atualizar(Gasto gasto) {
        ContentValues cv = new ContentValues();
        cv.put("descricao", gasto.getDescricao());
        cv.put("valor", gasto.getValor());
        cv.put("categoria", gasto.getCategoria());
        cv.put("parcela_atual", gasto.getParcelaAtual());
        cv.put("parcela_total", gasto.getParcelaTotal());
        cv.put("mes", gasto.getMes());
        try {
            String[] args = {String.valueOf(gasto.getId())};
            escreve.update(DBhelper.TABELA_GASTO, cv, "id = ?", args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletar(int id) {
        try {
            String[] args = {String.valueOf(id)};
            escreve.delete(DBhelper.TABELA_GASTO, "id = ?", args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Lista os gastos de um mês específico ("yyyy-MM"), mais recentes primeiro. */
    public List<Gasto> listarPorMes(String mes) {
        List<Gasto> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + DBhelper.TABELA_GASTO
                + " WHERE mes = ? ORDER BY data DESC";
        Cursor c = le.rawQuery(sql, new String[]{mes});
        while (c.moveToNext()) {
            lista.add(montar(c));
        }
        c.close();
        return lista;
    }

    /** Soma total de gastos de um mês. */
    public double totalDoMes(String mes) {
        double total = 0;
        String sql = "SELECT SUM(valor) FROM " + DBhelper.TABELA_GASTO + " WHERE mes = ?";
        Cursor c = le.rawQuery(sql, new String[]{mes});
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    /** Retorna o total por categoria no mês, preservando a ordem das categorias. */
    public Map<String, Double> totalPorCategoria(String mes) {
        Map<String, Double> mapa = new LinkedHashMap<>();
        for (String cat : Gasto.CATEGORIAS) {
            mapa.put(cat, 0.0);
        }
        String sql = "SELECT categoria, SUM(valor) FROM " + DBhelper.TABELA_GASTO
                + " WHERE mes = ? GROUP BY categoria";
        Cursor c = le.rawQuery(sql, new String[]{mes});
        while (c.moveToNext()) {
            mapa.put(c.getString(0), c.getDouble(1));
        }
        c.close();
        return mapa;
    }

    /** Lista todas as parcelas que ainda possuem parcelas restantes. */
    public List<Gasto> listarParcelasAtivas() {
        List<Gasto> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + DBhelper.TABELA_GASTO
                + " WHERE categoria = ? AND parcela_total > 0 ORDER BY data DESC";
        Cursor c = le.rawQuery(sql, new String[]{Gasto.CAT_PARCELA});
        while (c.moveToNext()) {
            Gasto g = montar(c);
            if (g.getParcelasRestantes() > 0) {
                lista.add(g);
            }
        }
        c.close();
        return lista;
    }

    private Gasto montar(Cursor c) {
        Gasto g = new Gasto();
        g.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        g.setDescricao(c.getString(c.getColumnIndexOrThrow("descricao")));
        g.setValor(c.getDouble(c.getColumnIndexOrThrow("valor")));
        g.setCategoria(c.getString(c.getColumnIndexOrThrow("categoria")));
        g.setParcelaAtual(c.getInt(c.getColumnIndexOrThrow("parcela_atual")));
        g.setParcelaTotal(c.getInt(c.getColumnIndexOrThrow("parcela_total")));
        g.setMes(c.getString(c.getColumnIndexOrThrow("mes")));
        g.setData(c.getLong(c.getColumnIndexOrThrow("data")));
        return g;
    }
}
