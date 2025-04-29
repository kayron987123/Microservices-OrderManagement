package com.gad.msvc_products.exception;

import com.gad.msvc_products.dto.DataResponse;
import com.gad.msvc_products.utils.FormatterDateTime;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
                        FormatterDateTime.dateTimeNowFormatted(),
                        errors));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<DataResponse> handlerProductNotFoundException(ProductNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        null));
    }

}
