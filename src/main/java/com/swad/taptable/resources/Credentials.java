package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.MalformedJSONException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the credentials provided by the user during login. This class is used to deserialize
 * the JSON payload of the login request, which must contain the user's email and password.
 *
 * @author SWAD Team
 */
public class Credentials extends AbstractResource {

  private final String email;

  private final String password;

  /**
   * Creates a new instance of {@code Credentials}.
   *
   * @param email the email of the user.
   * @param password the plain-text password of the user.
   */
  public Credentials(final String email, final String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  @Override
  protected void writeJSON(final OutputStream out) throws Exception {
    // Credentials are never serialized to JSON output.
  }

  /**
   * Parses a JSON input stream to create an instance of {@code Credentials}. The expected JSON
   * format is:
   *
   * <pre>
   * { "email": "mario@test.com", "password": "supersecret" }
   * </pre>
   *
   * Unknown fields are silently ignored.
   *
   * @param in the input stream containing the JSON payload.
   * @return an instance of {@code Credentials} with the email and password extracted from the JSON.
   * @throws IOException if there is an error reading from the input stream or parsing the JSON.
   */
  public static Credentials fromJSON(final InputStream in)
      throws IOException, MalformedJSONException {
    String jEmail = null;
    String jPassword = null;

    final JsonParser jp = JSON_FACTORY.createParser(in);

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
        continue;
      }

      switch (jp.currentName()) {
        case "email":
          jp.nextToken();
          jEmail = jp.getText();
          break;
        case "password":
          jp.nextToken();
          jPassword = jp.getText();
          break;
        default:
          throw new MalformedJSONException(
              String.format("Unexpected field '%s' in credentials JSON.", jp.currentName()));
      }
    }

    return new Credentials(jEmail, jPassword);
  }
}
