package com.example.gestaofinanceira.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda a configuração de trabalho usada para converter preço em horas de
 * trabalho: salário mensal e horas trabalhadas por mês.
 */
public final class ConfigTrabalho {

    private static final String PREFS = "config_trabalho";
    private static final String KEY_SALARIO = "salario";
    private static final String KEY_HORAS_MES = "horas_mes";

    /** Padrão: 44h semanais ≈ 220h/mês. */
    public static final double HORAS_MES_PADRAO = 220.0;
    /** Jornada usada para converter horas em dias de trabalho. */
    public static final double HORAS_POR_DIA = 8.0;

    private ConfigTrabalho() {
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static double getSalario(Context c) {
        return prefs(c).getFloat(KEY_SALARIO, 0f);
    }

    public static double getHorasMes(Context c) {
        return prefs(c).getFloat(KEY_HORAS_MES, (float) HORAS_MES_PADRAO);
    }

    public static void salvar(Context c, double salario, double horasMes) {
        prefs(c).edit()
                .putFloat(KEY_SALARIO, (float) salario)
                .putFloat(KEY_HORAS_MES, (float) horasMes)
                .apply();
    }

    /** True quando o usuário ainda não informou o salário. */
    public static boolean precisaConfigurar(Context c) {
        return getSalario(c) <= 0;
    }

    /** Valor de uma hora de trabalho (salário / horas por mês). */
    public static double valorHora(Context c) {
        double horas = getHorasMes(c);
        if (horas <= 0) {
            return 0;
        }
        return getSalario(c) / horas;
    }

    /** Converte um preço na quantidade de horas de trabalho equivalente. */
    public static double horasPara(Context c, double preco) {
        double vh = valorHora(c);
        if (vh <= 0) {
            return 0;
        }
        return preco / vh;
    }
}
