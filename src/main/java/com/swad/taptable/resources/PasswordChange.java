/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Resource representing a request to change a user's password, containing the current password and
 * the desired new password.
 */
public class PasswordChange extends AbstractResource {

  private final String currentPassword;
  private final String newPassword;

  /**
   * Creates a new password-change resource.
   *
   * @param currentPassword the user's current password.
   * @param newPassword the desired new password.
   */
  public PasswordChange(final String currentPassword, final String newPassword) {
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  /**
   * Returns the user's current password.
   *
   * @return the current password.
   */
  public String getCurrentPassword() {
    return currentPassword;
  }

  /**
   * Returns the desired new password.
   *
   * @return the new password.
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * Parses a {@link PasswordChange} from its JSON representation.
   *
   * @param in the input stream containing the JSON payload.
   * @return the parsed resource.
   * @throws IOException if an I/O error occurs while reading the stream.
   * @throws UnexpectedKeyException if the JSON contains an unrecognised field.
   */
  public static PasswordChange fromJSON(final InputStream in)
      throws IOException, UnexpectedKeyException {
    final JsonParser jp = JSON_FACTORY.createParser(in);
    String currentPassword = null;
    String newPassword = null;

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) continue;
      switch (jp.currentName()) {
        case "current_password":
          jp.nextToken();
          currentPassword = jp.getText();
          break;
        case "new_password":
          jp.nextToken();
          newPassword = jp.getText();
          break;
        default:
          throw new UnexpectedKeyException("Unexpected key: " + jp.currentName());
      }
    }

    return new PasswordChange(currentPassword, newPassword);
  }

  @Override
  protected void writeJSON(final OutputStream out) throws Exception {
    // Passwords are never serialized to the wire
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);
    jg.writeStartObject();
    jg.writeEndObject();
    jg.flush();
  }
}
