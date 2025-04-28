package com.gad.msvc_details_order.exception;

public class ProductFeignNotFoundException extends RuntimeException {
    public ProductFeignNotFoundException(String message) {
        super(message);
    }

    public ProductFeignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
