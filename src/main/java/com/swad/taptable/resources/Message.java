/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a generic message or an error message to be shown in the logger.
 *
 * <p>When used for error messages, the {@code errorCode} and {@code errorDetails} fields can be
 * used to provide additional information about the error, which can be helpful for debugging and
 * monitoring purposes.
 *
 * <p>In the context of the application, instances of this class can be used to encapsulate messages
 * that are returned in API responses, as well as messages that are logged for debugging and
 * monitoring purposes.
 *
 * @author SWAD Team
 */
public class Message extends AbstractResource {
  private final String message;
  private final String errorCode;
  private final String errorDetails;
  private final boolean isError;

  /**
   * Creates an error message.
   *
   * @param message the message.
   * @param errorCode the code of the error.
   * @param errorDetails additional details about the error.
   */
  public Message(final String message, final String errorCode, final String errorDetails) {
    this.message = message;
    this.errorCode = errorCode;
    this.errorDetails = errorDetails;
    this.isError = true;
  }

  /**
   * Creates a generic message.
   *
   * @param message the message.
   */
  public Message(final String message) {
    this.message = message;
    this.errorCode = null;
    this.errorDetails = null;
    this.isError = false;
  }

  /**
   * Returns the message.
   *
   * @return the message.
   */
  public final String getMessage() {
    return message;
  }

  /**
   * Returns the code of the error, if any.
   *
   * @return the code of the error, if any, {@code null} otherwise.
   */
  public final String getErrorCode() {
    return errorCode;
  }

  /**
   * Returns additional details about the error, if any.
   *
   * @return additional details about the error, if any, {@code null} otherwise.
   */
  public final String getErrorDetails() {
    return errorDetails;
  }

  /**
   * Indicates whether the message is about an error or not.
   *
   * @return {@code true} is the message is about an error, {@code false} otherwise.
   */
  public final boolean isError() {
    return isError;
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {

    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    jg.writeFieldName("message");

    jg.writeStartObject();

    jg.writeStringField("message", message);

    if (errorCode != null) {
      jg.writeStringField("error_code", errorCode);
    }

    if (errorDetails != null) {
      jg.writeStringField("error_details", errorDetails);
    }

    jg.writeEndObject();

    jg.writeEndObject();

    jg.flush();
  }
}
