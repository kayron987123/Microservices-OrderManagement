package com.gad.msvc_details_order.exception;

import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.utils.FormatterDateTime;
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
                        FormatterDateTime.dateTimeNowFormatted(),
                        errors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> fieldOrder = List.of("uuidDetail", "uuidOrder", "productName", "amount", "unitPrice");

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

    @ExceptionHandler(StockNotAvailableException.class)
    public ResponseEntity<DataResponse> handlerStockNotAvailableException(StockNotAvailableException ex) {
        return ResponseEntity.status(CONFLICT)
                .body(new DataResponse(CONFLICT.value(),
                        ex.getMessage(),
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<DataResponse> handlerOrderNotFoundException(OrderNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(ProductFeignNotFoundException.class)
    public ResponseEntity<DataResponse> handlerProductFeignNotFoundException(ProductFeignNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(OrderFeignNotFoundException.class)
    public ResponseEntity<DataResponse> handlerOrderFeignNotFoundException(OrderFeignNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        null));
    }
}
