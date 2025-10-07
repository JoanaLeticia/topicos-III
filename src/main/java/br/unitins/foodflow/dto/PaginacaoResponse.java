package br.unitins.foodflow.dto;

import java.util.List;

public class PaginacaoResponse<T> {
    private List<T> dados;
    private int paginaAtual;
    private int tamanhoPagina;
    private long totalRegistros;
    private int totalPaginas;

    public PaginacaoResponse(List<T> dados, int paginaAtual, int tamanhoPagina, long totalRegistros) {
        this.dados = dados;
        this.paginaAtual = paginaAtual;
        this.tamanhoPagina = tamanhoPagina;
        this.totalRegistros = totalRegistros;
        this.totalPaginas = (int) Math.ceil((double) totalRegistros / tamanhoPagina);
    }

    public List<T> getDados() {
        return dados;
    }

    public int getPaginaAtual() {
        return paginaAtual;
    }

    public int getTamanhoPagina() {
        return tamanhoPagina;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}
