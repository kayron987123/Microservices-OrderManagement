package com.gad.msvc_customer.dto;


public record DataResponse(
        int status,
        String message,
        Object data,
        String timestamp,
        Object errors
) {
}
