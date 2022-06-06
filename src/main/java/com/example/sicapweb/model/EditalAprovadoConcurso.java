package com.example.sicapweb.model;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.EditalVaga;

public class EditalAprovadoConcurso {

    //terceira aba de dados de envio eletronica de documentos-> admissao
    private String numeroEdital;
    private String codigoVaga;

    private EditalAprovado editalaprovado;
    private EditalVaga editalVaga;
    private String cpf;
    private String nome;
    private String numeroInscricao;
    private String classificacao;
    private String Situacao;

    public EditalAprovadoConcurso() {
    }

    public EditalAprovadoConcurso(String numeroEdital, String codigoVaga, EditalVaga editalVaga, String cpf, String nome, String numeroInscricao, String classificacao, String situacao) {
        this.numeroEdital = numeroEdital;
        this.codigoVaga = codigoVaga;
        this.editalVaga = editalVaga;
        this.cpf = cpf;
        this.nome = nome;
        this.numeroInscricao = numeroInscricao;
        this.classificacao = classificacao;
        Situacao = situacao;
    }

    public String getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(String numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public String getCodigoVaga() {
        return codigoVaga;
    }

    public void setCodigoVaga(String codigoVaga) {
        this.codigoVaga = codigoVaga;
    }

    public EditalVaga getEditalVaga() {
        return editalVaga;
    }

    public void setEditalVaga(EditalVaga editalVaga) {
        this.editalVaga = editalVaga;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumeroInscricao() {
        return numeroInscricao;
    }

    public void setNumeroInscricao(String numeroInscricao) {
        this.numeroInscricao = numeroInscricao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getSituacao() {
        return Situacao;
    }

    public void setSituacao(String situacao) {
        this.Situacao = situacao;
    }

    public EditalAprovado getEditalaprovado() {
        return editalaprovado;
    }

    public void setEditalaprovado(EditalAprovado editalaprovado) {
        this.editalaprovado = editalaprovado;
    }
}
