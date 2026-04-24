package com.swad.taptable.exception;

/**
 * Exception thrown when a JSON parsing error occurs due to malformed input. This is used to
 * differentiate between general IOExceptions and those specifically caused by invalid JSON
 * structure, allowing for more precise error handling and messaging in REST endpoints.
 * 
 * @author SWAD Team
 */
public class MalformedJSONException extends Exception {
  /**
   * Constructs a new MalformedJSONException with the specified detail message.
   * 
   * @param message the detail message explaining the reason for the exception
   */
  public MalformedJSONException(final String message) {
    super(message);
  }

  /**
   * Constructs a new MalformedJSONException with the specified cause.
   * 
   * @param cause the cause of the exception, typically the underlying JSON parsing exception
   */
  public MalformedJSONException(final Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new MalformedJSONException with the specified detail message and cause.
   * 
   * @param message the detail message explaining the reason for the exception
   * @param cause the cause of the exception, typically the underlying JSON parsing exception
   */
  public MalformedJSONException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new MalformedJSONException with a default message indicating that the JSON in the
   * request body is malformed. This can be used when the specific parsing error message is not
   * necessary or when a generic error message is sufficient for the client.
   */
  public MalformedJSONException() {
    super("Malformed JSON in request body.");
  }
}
