package com.example.orderservice.exception;

public class TooManyConcurrentRequestsException extends RuntimeException {
    public TooManyConcurrentRequestsException(String message) {
        super(message);
    }
}
