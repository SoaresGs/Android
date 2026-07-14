package com.example.gestaofinanceira.Util;

import android.content.Context;

import com.example.gestaofinanceira.Model.Gasto;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

/**
 * Gera um arquivo CSV (abre em Excel/Google Planilhas) com os gastos de um mês.
 */
public final class Exportador {

    /** Byte Order Mark: garante acentuação correta ao abrir no Excel. */
    private static final char BOM = '﻿';

    private Exportador() {
    }

    /**
     * Escreve os gastos em um arquivo CSV dentro do cache do app e o retorna.
     *
     * @return o arquivo gerado ou null em caso de erro.
     */
    public static File exportarCsv(Context context, String mes, List<Gasto> gastos) {
        File dir = new File(context.getCacheDir(), "exportacoes");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        File arquivo = new File(dir, "gastos_" + mes + ".csv");
        try (FileWriter w = new FileWriter(arquivo)) {
            w.write(BOM);
            w.write("Descrição;Categoria;Valor;Parcela;Mês\n");
            double total = 0;
            for (Gasto g : gastos) {
                String parcela = g.isParcela()
                        ? g.getParcelaAtual() + "/" + g.getParcelaTotal()
                        : "";
                w.write(escapar(g.getDescricao()) + ";"
                        + escapar(g.getCategoria()) + ";"
                        + String.format(Locale.getDefault(), "%.2f", g.getValor()) + ";"
                        + parcela + ";"
                        + g.getMes() + "\n");
                total += g.getValor();
            }
            w.write(";;" + String.format(Locale.getDefault(), "%.2f", total) + ";;\n");
            w.flush();
            return arquivo;
        } catch (Exception e) {
            return null;
        }
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(";", ",").replace("\n", " ");
    }
}
