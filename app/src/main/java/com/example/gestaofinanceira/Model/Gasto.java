package com.example.gestaofinanceira.Model;

import java.io.Serializable;

/**
 * Representa um gasto/despesa lançado pelo usuário.
 *
 * A categoria define a natureza do gasto e usa as constantes CAT_*.
 * Quando a categoria é PARCELA os campos parcelaAtual e parcelaTotal
 * são preenchidos para permitir o controle das parcelas restantes.
 */
public class Gasto implements Serializable {

    public static final String CAT_FIXO = "Fixo";
    public static final String CAT_VARIAVEL = "Variável";
    public static final String CAT_MERCADO = "Mercado";
    public static final String CAT_FAST_FOOD = "Fast Food";
    public static final String CAT_PARCELA = "Parcela";
    public static final String CAT_OUTROS = "Outros";

    public static final String[] CATEGORIAS = {
            CAT_FIXO, CAT_VARIAVEL, CAT_MERCADO, CAT_FAST_FOOD, CAT_PARCELA, CAT_OUTROS
    };

    private int id;
    private String descricao;
    private double valor;
    private String categoria;
    private int parcelaAtual;   // número da parcela atual (ex.: 3)
    private int parcelaTotal;   // total de parcelas (ex.: 10)
    private String mes;         // referência no formato "yyyy-MM"
    private long data;          // timestamp de criação

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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getParcelaAtual() {
        return parcelaAtual;
    }

    public void setParcelaAtual(int parcelaAtual) {
        this.parcelaAtual = parcelaAtual;
    }

    public int getParcelaTotal() {
        return parcelaTotal;
    }

    public void setParcelaTotal(int parcelaTotal) {
        this.parcelaTotal = parcelaTotal;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    /** Retorna true quando o gasto é do tipo parcelamento. */
    public boolean isParcela() {
        return CAT_PARCELA.equals(categoria) && parcelaTotal > 0;
    }

    /** Quantidade de parcelas ainda restantes (incluindo a atual). */
    public int getParcelasRestantes() {
        if (!isParcela()) {
            return 0;
        }
        int restantes = parcelaTotal - parcelaAtual + 1;
        return restantes < 0 ? 0 : restantes;
    }
}
