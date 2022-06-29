package com.example.sicapweb.model;

import br.gov.to.tce.annotation.JayValEnum;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;

import java.math.BigInteger;

public class ConcursoEnvioAssRetorno {
    private BigInteger id;

    private ConcursoEnvio concursoEnvio;

    @JayValEnum
    private Integer status;

    private String mensagem;

}
