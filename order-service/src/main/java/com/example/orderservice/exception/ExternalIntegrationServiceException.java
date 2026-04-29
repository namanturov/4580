package com.example.orderservice.exception;

public class ExternalIntegrationServiceException extends RuntimeException {
    public ExternalIntegrationServiceException(String message) {
        super(message);
    }
}