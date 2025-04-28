package com.gad.msvc_gateway.dto;

public record DataResponse(
        int status,
        String message,
        Object data,
        String timestamp,
        Object errors
) {
}
