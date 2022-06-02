package com.example.sicapweb.model;

import java.util.Date;

public class ProcessoAdmissaoConcurso {
    //primeira aba de dados de envio eletronica de documentos-> admissao
    private String numeroEdital;

    private Date dtcriacao;

    private Integer quantidade;

    private  String  Situacao;

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

    public String getSituacao() {
        return Situacao;
    }

    public void setSituacao(String situacao) {
        Situacao = situacao;
    }
}
