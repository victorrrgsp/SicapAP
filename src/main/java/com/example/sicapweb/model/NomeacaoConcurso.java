package com.example.sicapweb.model;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.pessoal.Admissao;
import br.gov.to.tce.model.ap.relacional.Ato;

public class NomeacaoConcurso {
    //segunda aba de dados de envio eletronica de documentos-> admissao
    private String numeroAto;

    private Ato ato;
    private String cpf;
    private String nome;
    private String numeroEdital;

    private Edital edital;
    private String SitCadAprovado;
    private String classificacao;
    private String vaga;

    private EditalAprovado editalAprovado;

    private Admissao admissao;
    private String ProcessoConcurso;
    private String SituacaoNomeacao;

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

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
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

    public String getSituacaoNomeacao() {
        return SituacaoNomeacao;
    }

    public void setSituacaoNomeacao(String situacaoNomeacao) {
        SituacaoNomeacao = situacaoNomeacao;
    }

    public Ato getAto() {
        return ato;
    }

    public void setAto(Ato ato) {
        this.ato = ato;
    }

    public Edital getEdital() {
        return edital;
    }

    public void setEdital(Edital edital) {
        this.edital = edital;
    }

    public EditalAprovado getEditalAprovado() {
        return editalAprovado;
    }

    public void setEditalAprovado(EditalAprovado editalAprovado) {
        this.editalAprovado = editalAprovado;
    }

    public Admissao getAdmissao() {
        return admissao;
    }

    public void setAdmissao(Admissao admissao) {
        this.admissao = admissao;
    }

}
