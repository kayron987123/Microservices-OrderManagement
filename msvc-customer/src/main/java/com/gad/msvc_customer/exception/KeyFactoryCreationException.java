package com.gad.msvc_customer.exception;

public class KeyFactoryCreationException extends RuntimeException{
    public KeyFactoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyFactoryCreationException(String message) {
        super(message);
    }
}
