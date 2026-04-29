package com.example.orderservice.exception;

public class SendingAsyncMessageException extends RuntimeException {
    public SendingAsyncMessageException(String message) {
        super(message);
    }
}
