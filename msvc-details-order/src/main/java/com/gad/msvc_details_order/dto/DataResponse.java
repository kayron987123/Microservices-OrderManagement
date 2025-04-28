package com.gad.msvc_details_order.dto;

public record DataResponse(
        int status,
        String message,
        Object data,
        String timestamp,
        Object errors
) {
}
