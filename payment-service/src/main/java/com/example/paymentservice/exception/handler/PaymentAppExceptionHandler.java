package com.example.paymentservice.exception.handler;

import com.example.orderservice.dto.response.Response;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.exception.IdempotencyProcessingException;
import com.example.paymentservice.exception.RequestDataValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentAppExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Response handleEntityNotFoundException(EntityNotFoundException e) {
        return Response.of(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RequestDataValidationException.class)
    public Response handleRequestDataValidation(RequestDataValidationException e) {
        return Response.of(e.getMessage());
    }

    @ExceptionHandler(IdempotencyProcessingException.class)
    public ResponseEntity<Response> handleIdempotencyProcessingException(IdempotencyProcessingException e) {
        return ResponseEntity
                .status(e.getResponseHttpStatus())
                .body(Response.of(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            MissingRequestHeaderException.class})
    public Response handleValidationExceptions(Exception ex) {
        var errorMessage = "ой что то не так передаем. (Писать обработку валидаций было лень)";
        return Response.of(errorMessage);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    protected Response handleException(Exception ex) {
        var errorMessage = "уже смотрю";
        log.error(ex.getMessage(), ex);
        return Response.of(errorMessage);
    }
}
