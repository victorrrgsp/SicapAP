package com.example.sicapweb.model;

public class Inciso {

    private String inciso;
    private String documento;
    private String descricao;
    private String status;
    private String obrigatorio;

    public Inciso(){}

    public Inciso(String inciso, String documento, String descricao, String status, String obrigatorio){
        this.inciso = inciso;
        this.documento = documento;
        this.descricao = descricao;
        this.status = status;
        this.obrigatorio = obrigatorio;
    }

    public String getInciso() {
        return inciso;
    }

    public void setInciso(String inciso) {
        this.inciso = inciso;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(String obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
}
