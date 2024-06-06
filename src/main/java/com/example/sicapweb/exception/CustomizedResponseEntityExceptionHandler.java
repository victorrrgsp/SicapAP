package com.example.sicapweb.exception;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ExceptionResponse> handleDataIntegrityViolationExceptions(DataIntegrityViolationException ex, WebRequest webRequest, HttpServletRequest httpRequest) {
        
		String mensagem = "";
        
        if (ex.getCause() != null && ex.getCause() instanceof ConstraintViolationException) {
			
			ConstraintViolationException cve = (ConstraintViolationException) ex.getCause();
            String constraintName = cve.getSQLException().getMessage(); // Obter detalhes do SQL exception para análise mais aprofundada
            //String constraintName = cve.getSQLException().getMessage(); // Obter detalhes do SQL exception para análise mais aprofundada

            if (constraintName.toUpperCase().contains("UK_")) { // uk_ prefix for unique key constraint
                //recupera tudo que estiver entre UK_{stringcapturada}_IDX
				//var uk = constraintName.toUpperCase().substring(constraintName.indexOf("UK_") + 3, constraintName.indexOf("_ID"));
				Matcher matcher = Pattern.compile("UK_(.*?)(_|\")").matcher(constraintName);
				String uk = "";
				if (matcher.find()) {
					uk = " verifique o campo :"+matcher.group(1);
				} 
                mensagem = "Um ou mais dados inseridos já existem no sistema. Por favor, verifique os dados para campos que devem ser únicos e tente novamente."+uk;
            } else if (constraintName.toUpperCase().contains("FK_")) { // fk_ prefix for foreign key constraint
				Matcher matcher =  Pattern.compile("FK_(.*?)(_|\")").matcher(constraintName.toUpperCase());
				String fk ="";
				if (matcher.find()) {
					fk =" verifique as associacoes dentro de :"+matcher.group(1);
				}
                mensagem = "Não é possível completar esta operação porque existem dependências vinculadas a outros registros."+fk;
            } else {
                mensagem += " Por favor, revise os dados inseridos, especialmente os campos que devem ser únicos ou que devem respeitar restrições específicas.";
            }
        } else {
            mensagem += " Verifique se todos os campos estão corretos e tente novamente. Se o problema persistir, entre em contato com o suporte técnico.";
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse(mensagem);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
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

	@ExceptionHandler(ChaveValidationException.class)
	public final ResponseEntity<ExceptionResponse> RemessaChaveExisteException(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage());
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
}
