package com.example.sicapweb.model;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalHomologacao;

import java.util.Date;

public class EditalFinalizado {

    private String numeroEdital;

    private Edital edital;
    private String processo;

    private Date data;

    public EditalFinalizado(String numeroEdital, String processo, Date data) {
        this.numeroEdital = numeroEdital;
        this.processo = processo;
        this.data = data;
    }

    public EditalFinalizado() {
    }

    public String getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(String numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Edital getEdital() {
        return edital;
    }

    public void setEdital(Edital edital) {
        this.edital = edital;
    }
}
