package com.example.orderservice.exception.handler;

import com.example.orderservice.dto.response.Response;
import com.example.orderservice.exception.EntityNotFoundException;
import com.example.orderservice.exception.ExternalIntegrationServiceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderAppExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.of(e.getMessage()));
    }

    @ExceptionHandler(ExternalIntegrationServiceException.class)
    public ResponseEntity<Response> handleExternalIntegrationServiceException(ExternalIntegrationServiceException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.of("ой, что-то пошло не так. Уже чиним, братан 😓."));
    }
}
