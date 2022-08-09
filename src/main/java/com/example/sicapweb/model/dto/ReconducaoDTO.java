package com.example.sicapweb.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ReconducaoDTO {

    private BigInteger id;
    private String cpfServidor;
    private String nome;
    private String cargo;
    private String numeroAto;
    private Integer status;
    private String processo;
}
