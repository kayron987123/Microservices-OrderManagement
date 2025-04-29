package com.gad.msvc_customer.exception;

import com.gad.msvc_customer.dto.DataResponse;
import com.gad.msvc_customer.utils.UtilsMethods;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> fieldOrder = List.of("name", "email", "password", "phone", "lastName");

        List<Map<String, Object>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                FieldError::getDefaultMessage,
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(e -> {
                    int index = fieldOrder.indexOf(e.getKey());
                    return index == -1 ? Integer.MAX_VALUE : index;
                }))
                .map(entry -> {
                    Map<String, Object> errorMap = new LinkedHashMap<>();
                    errorMap.put(TEXT_FIELD, entry.getKey());
                    errorMap.put(TEXT_MESSAGES, entry.getValue().stream().sorted().toList());
                    return errorMap;
                })
                .toList();

        return ResponseEntity.badRequest()
                .body(new DataResponse(BAD_REQUEST.value(),
                        "Validation incorrect",
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        errors));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<DataResponse> handlerCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(CustomerAlreadyExists.class)
    public ResponseEntity<DataResponse> handlerCustomerAlreadyExists(CustomerAlreadyExists ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(new DataResponse(BAD_REQUEST.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<DataResponse> handlerRoleNotFoundException(RoleNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
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
}
