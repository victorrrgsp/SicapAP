package com.example.sicapweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class MovimentacaoNotFaud extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public MovimentacaoNotFaud(String tipoRegistro,String CpfServido) {
		super("A movimentação de " + tipoRegistro + " do CPF " + CpfServido + " não tem processo no e-Contas com decisão julgada para o usuário atual!");
	}
	public MovimentacaoNotFaud(String mensagem) {
		super(mensagem);
	}

}
