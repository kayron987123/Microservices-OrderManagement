package com.gad.msvc_gateway.controller;

import com.gad.msvc_gateway.dto.DataResponse;
import com.gad.msvc_gateway.utils.FormatterDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/fallback")
public class GatewayFallbackController {
    private static final String FALLBACK_ERROR_KEY = "error";
    private static final String FALLBACK_MESSAGE_VALUE = "Fallback triggered at gateway";

    @PostMapping("/oauth")
    public ResponseEntity<DataResponse> oauthFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "OAuth service is temporarily unavailable.",
                null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        Map.of(FALLBACK_ERROR_KEY, FALLBACK_MESSAGE_VALUE)
        ));
    }

    @PostMapping("/orders")
    public ResponseEntity<DataResponse> ordersFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Orders service is temporarily unavailable.",
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        Map.of(FALLBACK_ERROR_KEY, FALLBACK_MESSAGE_VALUE)
                ));
    }

    @PostMapping("/order-details")
    public ResponseEntity<DataResponse> orderDetailsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Order Details service is temporarily unavailable.",
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        Map.of(FALLBACK_ERROR_KEY, FALLBACK_MESSAGE_VALUE)
                ));
    }

    @PostMapping("/products")
    public ResponseEntity<DataResponse> productsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Products service is temporarily unavailable.",
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        Map.of(FALLBACK_ERROR_KEY, FALLBACK_MESSAGE_VALUE)
                ));
    }

    @PostMapping("/customers")
    public ResponseEntity<DataResponse> customersFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Customers service is temporarily unavailable.",
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        Map.of(FALLBACK_ERROR_KEY, FALLBACK_MESSAGE_VALUE)
                ));
    }
}
