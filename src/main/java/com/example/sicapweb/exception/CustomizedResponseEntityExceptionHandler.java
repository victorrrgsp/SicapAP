package com.example.sicapweb.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import org.springframework.http.HttpHeaders;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
		ExceptionResponse exceptionResponse =
		new ExceptionResponse(
			"Verifique os campos e tente novamente. Caso o erro persista, entre em contato com o suporte."
		);
		return new ResponseEntity<>(exceptionResponse, status);
	}
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = 
				new ExceptionResponse(
						//new Date(),
						ex.getMessage()
						//request.getDescription(true)
						);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@ExceptionHandler(InvalitInsert.class)
	public final ResponseEntity<ExceptionResponse> InvalitInsertExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = 
				new ExceptionResponse(
						//new Date(),
						ex.getMessage()
						//request.getDescription(true)
						);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(MovimentacaoNotFaud.class)
	public final ResponseEntity<ExceptionResponse> MovimentacaoNotFaudExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = 
				new ExceptionResponse(
						//new Date(),
						ex.getMessage()
						//request.getDescription(true)
						);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.PRECONDITION_FAILED);
	}
	@ExceptionHandler(NonRPPSAccessException.class)
	public final ResponseEntity<ExceptionResponse> NonRPPSAccessExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = 
				new ExceptionResponse(
						//new Date(),
						ex.getMessage()
						//request.getDescription(true)
						);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
	}
	@ExceptionHandler(LoginExpiradoException.class)
	public final ResponseEntity<ExceptionResponse> LoginExpiradoExceptionExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = 
				new ExceptionResponse(
						//new Date(),
						ex.getMessage()
						//request.getDescription(true)
						);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
	}
}
