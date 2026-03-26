package com.example.paymentservice.exception;

public class RequestDataValidationException extends RuntimeException {
    public RequestDataValidationException(String message) {
        super(message);
    }
}

