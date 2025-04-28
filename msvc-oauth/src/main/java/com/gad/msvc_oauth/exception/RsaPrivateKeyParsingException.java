package com.gad.msvc_oauth.exception;

public class RsaPrivateKeyParsingException extends RuntimeException {
    public RsaPrivateKeyParsingException(String message) {
        super(message);
    }
    public RsaPrivateKeyParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
