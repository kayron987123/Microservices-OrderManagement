package com.gad.msvc_orders.msvc_orders.exception;

public class OrderDetailFeignNotFoundException extends RuntimeException {
    public OrderDetailFeignNotFoundException(String message) {
        super(message);
    }

    public OrderDetailFeignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
