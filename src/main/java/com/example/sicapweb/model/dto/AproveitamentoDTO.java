package com.example.sicapweb.model.dto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter @Setter
public class AproveitamentoDTO {

    private BigDecimal id;
    private String cpfServidor;
    private String nome;
    private String cargo;
    private String numeroAto;
    private Integer status;

}
