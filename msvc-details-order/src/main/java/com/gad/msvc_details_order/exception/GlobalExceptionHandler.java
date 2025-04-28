package com.gad.msvc_details_order.exception;

import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.utils.FormatterDateTime;
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
                .map(entry -> Map.of(
                        "field", entry.getKey(),
                        "messages", entry.getValue().stream().sorted().toList()
                ))
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
