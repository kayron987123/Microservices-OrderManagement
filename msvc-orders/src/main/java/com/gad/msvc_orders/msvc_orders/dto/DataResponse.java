package com.gad.msvc_orders.msvc_orders.dto;

public record DataResponse(
        int status,
        String message,
        Object data,
        String timestamp,
        Object errors
) {
}
