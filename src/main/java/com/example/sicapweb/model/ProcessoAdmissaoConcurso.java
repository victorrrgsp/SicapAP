package com.example.sicapweb.model;

import java.math.BigInteger;
import java.util.Date;

public class ProcessoAdmissaoConcurso {
    //primeira aba de dados de envio eletronica de documentos-> admissao

    private BigInteger id;
    private String numeroEdital;

    private Date dtcriacao;


    private String  Processo;

    private Integer quantidade;

    private Integer status;

    public ProcessoAdmissaoConcurso() {
    }

    public String getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(String numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public Date getDtcriacao() {
        return dtcriacao;
    }

    public void setDtcriacao(Date dtcriacao) {
        this.dtcriacao = dtcriacao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProcesso() {
        return Processo;
    }

    public void setProcesso(String processo) {
        Processo = processo;
    }
}
