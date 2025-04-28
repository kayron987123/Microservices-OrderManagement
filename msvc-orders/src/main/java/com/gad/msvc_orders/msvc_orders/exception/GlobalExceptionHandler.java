package com.gad.msvc_orders.msvc_orders.exception;

import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DataResponse> handlerValidationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations() == null ?
                Collections.singletonList(ex.getMessage()) :
                ex.getConstraintViolations()
                        .stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .toList();

        return ResponseEntity.badRequest()
                .body(new DataResponse(BAD_REQUEST.value(),
                        "Validation incorrect",
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        errors));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<DataResponse> handlerOrderNotFoundException(OrderNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(OrderDetailNotFoundException.class)
    public ResponseEntity<DataResponse> handlerOrderDetailNotFoundException(OrderDetailNotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(new DataResponse(BAD_REQUEST.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(JwtDecodingException.class)
    public ResponseEntity<DataResponse> handlerJwtDecodingException(JwtDecodingException ex) {
        int statusCode;

        if ("JWT could not be decoded".equals(ex.getMessage()) || "Error loading public key".equals(ex.getMessage())) {
            statusCode = UNAUTHORIZED.value();
        } else if ("Invalid public key".equals(ex.getMessage())) {
            statusCode = INTERNAL_SERVER_ERROR.value();
        } else {
            statusCode = BAD_REQUEST.value();
        }

        return ResponseEntity.status(statusCode)
                .body(new DataResponse(statusCode,
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(KeyFactoryCreationException.class)
    public ResponseEntity<DataResponse> handlerKeyFactoryCreationException(KeyFactoryCreationException ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new DataResponse(INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(OrderDetailFeignNotFoundException.class)
    public ResponseEntity<DataResponse> handlerOrderDetailFeignNotFoundException(OrderDetailFeignNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }
}
