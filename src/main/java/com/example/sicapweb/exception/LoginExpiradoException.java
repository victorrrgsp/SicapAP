package com.example.sicapweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class  LoginExpiradoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public  LoginExpiradoException() {
		super("Login expirado! Faça login novamente!");
	}

}

