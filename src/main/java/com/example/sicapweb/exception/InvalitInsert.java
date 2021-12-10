package com.example.sicapweb.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalitInsert extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalitInsert(String exception) {
		super(exception);
	}
	
}
