package com.swad.taptable.exception.json;

public class UnexpectedKeyException extends Exception {
  public UnexpectedKeyException(String message) {
    super(message);
  }

  public UnexpectedKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnexpectedKeyException(Throwable cause) {
    super(cause);
  }

  public UnexpectedKeyException() {
    super("Unexpected key in JSON input");
  }
}
