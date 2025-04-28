package com.gad.msvc_oauth.exception;

import com.gad.msvc_oauth.dto.DataResponse;
import com.gad.msvc_oauth.utils.UtilsMethods;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomerFeignNotFoundException.class)
    public ResponseEntity<DataResponse> handlerCustomerFeignNotFoundException(CustomerFeignNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<DataResponse> handlerUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(new DataResponse(NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(RsaPrivateKeyParsingException.class)
    public ResponseEntity<DataResponse> handlerRsaPrivateKeyParsingException(RsaPrivateKeyParsingException ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new DataResponse(INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }

    @ExceptionHandler(RsaPublicKeyParsingException.class)
    public ResponseEntity<DataResponse> handlerRsaPublicKeyParsingException(RsaPublicKeyParsingException ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new DataResponse(INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        null));
    }
}
