package com.example.sicapweb.model;

public class NomeacaoConcurso {
    //segunda aba de dados de envio eletronica de documentos-> admissao
    private String numeroAto;
    private String cpf;
    private String nome;
    private String numeroEdital;
    private String SitCadAprovado;
    private Integer classificacao;
    private String vaga;
    private String ProcessoConcurso;
    private String StuacaoNomeacao;

    public NomeacaoConcurso() {
    }

    public String getNumeroAto() {
        return numeroAto;
    }

    public void setNumeroAto(String numeroAto) {
        this.numeroAto = numeroAto;
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

    public String getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(String numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public String getSitCadAprovado() {
        return SitCadAprovado;
    }

    public void setSitCadAprovado(String sitCadAprovado) {
        SitCadAprovado = sitCadAprovado;
    }

    public Integer getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Integer classificacao) {
        this.classificacao = classificacao;
    }

    public String getVaga() {
        return vaga;
    }

    public void setVaga(String vaga) {
        this.vaga = vaga;
    }

    public String getProcessoConcurso() {
        return ProcessoConcurso;
    }

    public void setProcessoConcurso(String processoConcurso) {
        ProcessoConcurso = processoConcurso;
    }

    public String getStuacaoNomeacao() {
        return StuacaoNomeacao;
    }

    public void setStuacaoNomeacao(String stuacaoNomeacao) {
        StuacaoNomeacao = stuacaoNomeacao;
    }
}
