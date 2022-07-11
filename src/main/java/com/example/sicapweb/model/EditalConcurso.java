package com.example.sicapweb.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class EditalConcurso{

        private BigInteger id;

    private Integer tipoEdital;
    private String numeroEdital;

    private String processo;

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    private Date dataPublicacao;
    private Date dataInicioInscricoes;
    private Date dataFimInscricoes;
    private String prazoValidade;
    private String veiculoPublicacao;
    private String situacao;
    private String cnpjEmpresaOrganizadora;
    private String nomEmpresaOrganizadora;
    private BigDecimal valorContratacao;

    public EditalConcurso(){

    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Integer getTipoEdital() {
        return tipoEdital;
    }

    public void setTipoEdital(Integer tipoEdital) {
        this.tipoEdital = tipoEdital;
    }

    public String getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(String numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Date getDataInicioInscricoes() {
        return dataInicioInscricoes;
    }

    public void setDataInicioInscricoes(Date dataInicioInscricoes) {
        this.dataInicioInscricoes = dataInicioInscricoes;
    }

    public Date getDataFimInscricoes() {
        return dataFimInscricoes;
    }

    public void setDataFimInscricoes(Date dataFimInscricoes) {
        this.dataFimInscricoes = dataFimInscricoes;
    }

    public String getPrazoValidade() {
        return prazoValidade;
    }

    public void setPrazoValidade(String prazoValidade) {
        this.prazoValidade = prazoValidade;
    }

    public String getVeiculoPublicacao() {
        return veiculoPublicacao;
    }

    public void setVeiculoPublicacao(String veiculoPublicacao) {
        this.veiculoPublicacao = veiculoPublicacao;
    }

    public String getCnpjEmpresaOrganizadora() {
        return cnpjEmpresaOrganizadora;
    }

    public void setCnpjEmpresaOrganizadora(String cnpjEmpresaOrganizadora) {
        this.cnpjEmpresaOrganizadora = cnpjEmpresaOrganizadora;
    }

    public String getNomEmpresaOrganizadora() {
        return nomEmpresaOrganizadora;
    }

    public void setNomEmpresaOrganizadora(String nomEmpresaOrganizadora) {
        this.nomEmpresaOrganizadora = nomEmpresaOrganizadora;
    }

    public BigDecimal getValorContratacao() {
        return valorContratacao;
    }

    public void setValorContratacao(BigDecimal valorContratacao) {
        this.valorContratacao = valorContratacao;
    }


    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
