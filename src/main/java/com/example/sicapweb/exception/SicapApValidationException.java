package com.example.sicapweb.exception;

public class SicapApValidationException extends RuntimeException {

    private String fieldName;

    public SicapApValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}