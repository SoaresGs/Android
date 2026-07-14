package com.example.gestaofinanceira.Util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utilitários de formatação de moeda e de mês de referência.
 */
public final class MoedaUtil {

    private static final Locale BR = new Locale("pt", "BR");

    private MoedaUtil() {
    }

    /** Formata um valor double como moeda brasileira (ex.: R$ 1.234,56). */
    public static String formatar(double valor) {
        return NumberFormat.getCurrencyInstance(BR).format(valor);
    }

    /** Retorna o mês atual no formato "yyyy-MM". */
    public static String mesAtual() {
        return new SimpleDateFormat("yyyy-MM", BR).format(Calendar.getInstance().getTime());
    }

    /** Aplica um deslocamento em meses a partir de um mês "yyyy-MM". */
    public static String deslocarMes(String mes, int offset) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", BR);
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(mes));
            c.add(Calendar.MONTH, offset);
            return sdf.format(c.getTime());
        } catch (Exception e) {
            return mesAtual();
        }
    }

    /** Converte "yyyy-MM" para um rótulo amigável (ex.: "Julho 2026"). */
    public static String rotuloMes(String mes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", BR);
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(mes));
            String nome = new SimpleDateFormat("MMMM", BR).format(c.getTime());
            nome = nome.substring(0, 1).toUpperCase(BR) + nome.substring(1);
            return nome + " " + c.get(Calendar.YEAR);
        } catch (Exception e) {
            return mes;
        }
    }
}
