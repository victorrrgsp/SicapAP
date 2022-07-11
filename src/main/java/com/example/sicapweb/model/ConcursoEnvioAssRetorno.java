package com.example.sicapweb.model;

import br.gov.to.tce.annotation.JayValEnum;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;

import java.math.BigInteger;

public class ConcursoEnvioAssRetorno {

    private ConcursoEnvio concursoEnvio;

    private Integer statusAssinatura;

    public ConcursoEnvioAssRetorno() {
    }

    public ConcursoEnvio getConcursoEnvio() {
        return concursoEnvio;
    }

    public void setConcursoEnvio(ConcursoEnvio concursoEnvio) {
        this.concursoEnvio = concursoEnvio;
    }

    public Integer getStatusAssinatura() {
        return statusAssinatura;
    }

    public void setStatusAssinatura(Integer statusAssinatura) {
        this.statusAssinatura = statusAssinatura;
    }

}
