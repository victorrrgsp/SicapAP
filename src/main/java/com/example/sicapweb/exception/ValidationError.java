package com.example.sicapweb.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationError {
    private String code;
    private String message;
    private List<Error> fieldErrors = new ArrayList<Error>();

    public ValidationError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addFieldError(String field, String errorMessage) {
        fieldErrors.add(new Error(field,errorMessage));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Error> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<Error> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

}

class Error{

    public String field;
    public String message;

    public Error(String field, String message) {
        this.field = field;
        this.message = message;
    }


}
