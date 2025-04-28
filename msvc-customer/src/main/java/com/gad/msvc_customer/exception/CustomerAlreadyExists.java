package com.gad.msvc_customer.exception;

public class CustomerAlreadyExists extends RuntimeException {
    public CustomerAlreadyExists(String message) {
        super(message);
    }
}
