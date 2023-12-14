package com.example.sicapweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NonRPPSAccessException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NonRPPSAccessException() {
		super("somente RPPS pode efetuar o envio de consessorios");
	}
    // enum RecursosExclusivosRPPS {
    //     CONOSSESORIOS("conssesorios");
    //     private String recurso;
    //     RecursosExclusivosRPPS(String recurso) {
    //         this.recurso = recurso;
    //     }

    // }

}
