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
 * Resource representing a request to change a user's role
 *
 * @author SWAD Team
 */
public class RoleChange extends AbstractResource {

  private final UserRole role;

  /**
   * Creates a new role-change resource.
   *
   * @param role the target role.
   */
  public RoleChange(final UserRole role) {
    this.role = role;
  }

  /**
   * Returns the target role.
   *
   * @return the role.
   */
  public UserRole getRole() {
    return role;
  }

  /**
   * Parses a {@link RoleChange} from its JSON representation.
   *
   * @param in the input stream containing the JSON payload.
   * @return the parsed resource.
   * @throws IOException if an I/O error occurs while reading the stream.
   * @throws UnexpectedKeyException if the JSON contains an unrecognized field.
   */
  public static RoleChange fromJSON(final InputStream in)
      throws IOException, UnexpectedKeyException {
    final JsonParser jp = JSON_FACTORY.createParser(in);
    UserRole role = null;

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) continue;
      switch (jp.currentName()) {
        case "role":
          jp.nextToken();
          role = UserRole.valueOf(jp.getText());
          break;
        default:
          throw new UnexpectedKeyException("Unexpected key: " + jp.currentName());
      }
    }

    return new RoleChange(role);
  }

  @Override
  protected void writeJSON(final OutputStream out) throws Exception {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);
    jg.writeStartObject();
    if (role != null) jg.writeStringField("role", role.name());
    jg.writeEndObject();
    jg.flush();
  }
}
