package com.gad.msvc_details_order.exception;

public class OrderFeignNotFoundException extends RuntimeException{
    public OrderFeignNotFoundException(String message) {
        super(message);
    }

    public OrderFeignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
