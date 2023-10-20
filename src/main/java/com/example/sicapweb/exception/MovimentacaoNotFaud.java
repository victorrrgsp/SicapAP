package com.example.sicapweb.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class MovimentacaoNotFaud extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public MovimentacaoNotFaud(String tipoRegistro,String CpfServido) {
    super("A movimenta��o de "+tipoRegistro+" do CPF "+CpfServido+" n�o tem processo no e-Contas com decis�o julgada para o usu�rio atual!");
	}
	
}
