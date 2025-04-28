package com.gad.msvc_orders.msvc_orders.exception;

public class OrderDetailNotFoundException extends RuntimeException {
    public OrderDetailNotFoundException(String message) {
        super(message);
    }
}
