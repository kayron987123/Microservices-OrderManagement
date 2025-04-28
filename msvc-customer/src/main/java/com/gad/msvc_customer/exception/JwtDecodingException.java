package com.gad.msvc_customer.exception;

public class JwtDecodingException extends RuntimeException{
    public JwtDecodingException(String message) {
        super(message);
    }
    public JwtDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
