package com.gad.msvc_oauth.exception;

public class RsaPublicKeyParsingException extends RuntimeException {
    public RsaPublicKeyParsingException(String message) {
        super(message);
    }

    public RsaPublicKeyParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
