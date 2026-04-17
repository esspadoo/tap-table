package com.swad.taptable.exception;

public class MalformedJSONException extends Exception {
    public MalformedJSONException(final String message) {
        super(message);
    }

    public MalformedJSONException(final Throwable cause) {
        super(cause);
    }

    public MalformedJSONException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MalformedJSONException() {
        super("Malformed JSON in request body.");
    }
}