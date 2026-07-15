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

    /** Formata uma quantidade de horas como "Xh Ymin" (ex.: 2h 30min). */
    public static String formatarHoras(double horas) {
        if (horas <= 0) {
            return "0h";
        }
        int h = (int) horas;
        int min = (int) Math.round((horas - h) * 60);
        if (min == 60) {
            h += 1;
            min = 0;
        }
        if (h == 0) {
            return min + "min";
        }
        return min > 0 ? h + "h " + min + "min" : h + "h";
    }

    /**
     * Descreve o custo em tempo de trabalho de forma amigável, incluindo dias
     * quando o total passa de uma jornada (ex.: "1 dia e 4h de trabalho").
     */
    public static String tempoDeTrabalho(double horas, double horasPorDia) {
        if (horas <= 0) {
            return "menos de 1 minuto";
        }
        if (horasPorDia > 0 && horas >= horasPorDia) {
            int dias = (int) (horas / horasPorDia);
            double resto = horas - dias * horasPorDia;
            String base = dias + (dias == 1 ? " dia" : " dias");
            if (resto >= 0.1) {
                return base + " e " + formatarHoras(resto);
            }
            return base;
        }
        return formatarHoras(horas);
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
