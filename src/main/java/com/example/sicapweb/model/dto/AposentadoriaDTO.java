package com.example.sicapweb.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter @Setter
public class AposentadoriaDTO {

    private BigInteger id;

    private String cpfServidor;

    private String nome;

    private String cargo;

    private Integer tipoAposentadoria;

    private String numeroAto;

    private Integer status;
}
