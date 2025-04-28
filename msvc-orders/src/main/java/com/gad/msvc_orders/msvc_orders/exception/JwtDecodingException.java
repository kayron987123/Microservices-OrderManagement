package com.gad.msvc_orders.msvc_orders.exception;

public class JwtDecodingException extends RuntimeException {
    public JwtDecodingException(String message) {
        super(message);
    }

    public JwtDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
