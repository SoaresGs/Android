package com.example.gestaofinanceira.Model;

import java.io.Serializable;

/**
 * Representa uma decisão de compra do módulo "Consumo Consciente" (inspirado
 * no TapDin): um item avaliado pelo seu custo em horas de trabalho.
 */
public class Decisao implements Serializable {

    public static final String STATUS_COMPRADO = "COMPRADO";
    public static final String STATUS_NAO_COMPRADO = "NAO_COMPRADO";
    public static final String STATUS_ESPERANDO = "ESPERANDO";

    private int id;
    private String descricao;
    private double valor;
    private double horas;       // horas de trabalho equivalentes
    private String status;
    private long decidirEm;     // timestamp em que a espera termina (0 se não espera)
    private long data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getHoras() {
        return horas;
    }

    public void setHoras(double horas) {
        this.horas = horas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDecidirEm() {
        return decidirEm;
    }

    public void setDecidirEm(long decidirEm) {
        this.decidirEm = decidirEm;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    /** True quando está em espera e o prazo ainda não chegou. */
    public boolean isAguardando() {
        return STATUS_ESPERANDO.equals(status) && System.currentTimeMillis() < decidirEm;
    }

    /** True quando está em espera e já pode ser decidida. */
    public boolean isProntaParaDecidir() {
        return STATUS_ESPERANDO.equals(status) && System.currentTimeMillis() >= decidirEm;
    }
}
