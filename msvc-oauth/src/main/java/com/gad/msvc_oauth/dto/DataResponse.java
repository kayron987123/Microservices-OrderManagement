package com.gad.msvc_oauth.dto;

import com.gad.msvc_oauth.model.Customer;

public record DataResponse(
        int status,
        String message,
        Customer data,
        String timestamp,
        Object errors
) {
}
