package com.example.sicapweb.model;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.relacional.Ato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class EditalHomologaConcurso {

    private BigInteger id;

    private String numeroEdital;

    private Edital edital;

    public String numeroAto;

    private Integer tipoAto;

    private Ato ato;

    private String veiculoPublicacao;

    private Date dataHomologacao;

    private Date dataPublicacao;

    private String situacao;

    public EditalHomologaConcurso(){
    }

    public EditalHomologaConcurso(BigInteger id, String numeroEdital, String numeroAto, Integer tipoAto, Ato ato, String veiculoPublicacao, Date dataPublicacao, String situacao) {
        this.id = id;
        this.numeroEdital = numeroEdital;
        this.numeroAto = numeroAto;
        this.tipoAto = tipoAto;
        this.ato = ato;
        this.veiculoPublicacao = veiculoPublicacao;
        this.dataPublicacao = dataPublicacao;
        this.situacao = situacao;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Date getDataHomologacao() {
        return dataHomologacao;
    }

    public void setDataHomologacao(Date dataHomologacao) {
        this.dataHomologacao = dataHomologacao;
    }

    public Edital getEdital() {
        return edital;
    }

    public void setEdital(Edital edital) {
        this.edital = edital;
    }

    public String getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(String numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public String getNumeroAto() {
        return numeroAto;
    }

    public void setNumeroAto(String numeroAto) {
        this.numeroAto = numeroAto;
    }

    public Integer getTipoAto() {
        return tipoAto;
    }

    public void setTipoAto(Integer tipoAto) {
        this.tipoAto = tipoAto;
    }

    public Ato getAto() {
        return ato;
    }

    public void setAto(Ato ato) {
        this.ato = ato;
    }

    public String getVeiculoPublicacao() {
        return veiculoPublicacao;
    }

    public void setVeiculoPublicacao(String veiculoPublicacao) {
        this.veiculoPublicacao = veiculoPublicacao;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
