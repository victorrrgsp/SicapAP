package com.example.sicapweb.model;

import br.gov.to.tce.annotation.JayValEnum;
import br.gov.to.tce.model.DefaultEnum;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;

import java.io.Serializable;
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

    public enum Status implements DefaultEnum<ConcursoEnvioAssRetorno.Status>, Serializable {
        Pendente(1, "Pendente"),
        Assinado(2, "Assinado");
        private final Integer valor;
        private final String label;

        Status(Integer valorOpcao, String labelOpcao) {
            valor = valorOpcao;
            label = labelOpcao;
        }

        public Integer getValor() {
            return valor;
        }

        public String getLabel() {
            return label;
        }

        public String toString() {
            return (this.label);
        }
    }

}
