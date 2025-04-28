package com.gad.msvc_oauth.exception;

public class CustomerFeignNotFoundException extends RuntimeException{
    public CustomerFeignNotFoundException(String message) {
        super(message);
    }

    public CustomerFeignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
