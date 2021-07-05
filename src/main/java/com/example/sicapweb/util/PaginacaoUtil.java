package com.example.sicapweb.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PaginacaoUtil<T> {

    private int tamanho;

    private int pagina;

    private long totalPaginas;

    private String direcao;

    private List<T> registros;

    public PaginacaoUtil(int tamanho, int pagina, long totalPaginas, String direcao, List<T> registros) {
        this.tamanho = tamanho;
        this.pagina = pagina;
        this.totalPaginas = totalPaginas;
        this.direcao = direcao;
        this.registros = registros;
    }
}
