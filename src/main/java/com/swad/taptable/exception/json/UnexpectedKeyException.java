/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.exception.json;

/**
 * Exception thrown when a JSON object contains an unexpected key
 *
 * @author SWAD Team
 */
public class UnexpectedKeyException extends Exception {

  /**
   * Creates a new exception with the given message.
   *
   * @param message the detail message.
   */
  public UnexpectedKeyException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with the given message and cause.
   *
   * @param message the detail message.
   * @param cause the underlying cause.
   */
  public UnexpectedKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new exception wrapping the given cause.
   *
   * @param cause the underlying cause.
   */
  public UnexpectedKeyException(Throwable cause) {
    super(cause);
  }

  /** Creates a new exception with a default message. */
  public UnexpectedKeyException() {
    super("Unexpected key in JSON input");
  }
}
