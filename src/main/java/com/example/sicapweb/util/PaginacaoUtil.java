package com.example.sicapweb.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PaginacaoUtil<T> {

    private int tamanho;

    private int pagina;

    private long totalPaginas;

    private long totalRegistros;

    private List<T> registros;

    public PaginacaoUtil(int tamanho, int pagina, long totalPaginas, long totalRegistros, List<T> registros) {
        this.tamanho = tamanho;
        this.pagina = pagina;
        this.totalPaginas = totalPaginas;
        this.registros = registros;
        this.totalRegistros = totalRegistros;
    }
}
