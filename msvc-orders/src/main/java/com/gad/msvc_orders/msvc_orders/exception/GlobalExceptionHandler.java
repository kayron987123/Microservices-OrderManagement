package com.gad.msvc_orders.msvc_orders.exception;

import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String TEXT_FIELD = "field";
    private static final String TEXT_MESSAGES = "messages";
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DataResponse> handlerValidationException(ConstraintViolationException ex) {
        List<Map<String, Object>> errors;

        if (ex.getConstraintViolations() == null) {
            errors = List.of(Map.of(
                    TEXT_FIELD, "unknown",
                    TEXT_MESSAGES, List.of(ex.getMessage())
            ));
        } else {
            errors = ex.getConstraintViolations()
                    .stream()
                    .collect(Collectors.groupingBy(
                            violation -> {
                                String path = violation.getPropertyPath().toString();
                                int lastDot = path.lastIndexOf('.');
                                return lastDot != -1 ? path.substring(lastDot + 1) : path;
                            },
                            LinkedHashMap::new,
                            Collectors.mapping(
                                    ConstraintViolation::getMessage,
                                    Collectors.toList()
                            )
                    ))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        Map<String, Object> errorMap = new LinkedHashMap<>();
                        errorMap.put(TEXT_FIELD, entry.getKey());
                        errorMap.put(TEXT_MESSAGES, entry.getValue().stream().sorted().toList());
                        return errorMap;
                    })
                    .toList();
        }

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
