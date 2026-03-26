package com.example.paymentservice.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdempotencyProcessingException extends RuntimeException {

    HttpStatus responseHttpStatus;

    public IdempotencyProcessingException(String message,
                                          HttpStatus responseHttpStatus) {
        super(message);
        this.responseHttpStatus = responseHttpStatus;
    }
}