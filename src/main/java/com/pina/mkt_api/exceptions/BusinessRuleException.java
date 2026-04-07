package com.pina.mkt_api.exceptions;

// Herda de RuntimeException para não nos obrigar a colocar "throws" em todos os métodos
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}