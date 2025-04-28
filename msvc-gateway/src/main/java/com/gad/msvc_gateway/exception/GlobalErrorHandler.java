package com.gad.msvc_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gad.msvc_gateway.dto.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Order(-2)
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    public GlobalErrorHandler() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    @NonNull
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Service Unavailable",
                ex.getMessage(),
                LocalDateTime.now()
        );

        String errorJson;
        try {
            errorJson = objectMapper.writeValueAsString(errorResponse);
            byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
