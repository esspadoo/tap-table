/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.exception;

/**
 * Exception thrown when an attempt is made to create or update an order with invalid data
 *
 * @author SWAD Team
 */
public class NotValidOrderException extends RuntimeException {

  /**
   * Creates a new exception with the given message.
   *
   * @param message the detail message.
   */
  public NotValidOrderException(String message) {
    super(message);
  }
}
