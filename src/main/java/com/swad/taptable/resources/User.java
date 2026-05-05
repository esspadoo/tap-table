/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * Representation of the user's resource.
 *
 * <p>Use {@link Builder} to construct instances. Two typical paths:
 *
 * <ul>
 *   <li>Input (registration): built via {@link #fromJSON} - carries a plain-text {@code password},
 *       no {@code passwordHash}, no timestamps.
 *   <li>DB read: set {@code passwordHash} and timestamps via the builder instead.
 * </ul>
 *
 * @author SWAD Team
 */
public class User extends AbstractResource {
  private final Integer id;
  private final String username;
  private final String email;
  private final String name;
  private final String surname;
  private final String phoneNumber;
  private final UserRole role;
  private final String password;
  private final String passwordHash;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Creates a new {@code User} from the values collected by the builder.
   *
   * @param b the builder containing the user data.
   */
  private User(final Builder b) {
    this.id = b.id;
    this.username = b.username;
    this.email = b.email;
    this.name = b.name;
    this.surname = b.surname;
    this.phoneNumber = b.phoneNumber;
    this.role = b.role;
    this.password = b.password;
    this.passwordHash = b.passwordHash;
    this.createdAt = b.createdAt;
    this.updatedAt = b.updatedAt;
  }

  /**
   * Returns the user identifier.
   *
   * @return the user identifier.
   */
  public Integer getId() {
    return id;
  }

  /**
   * Returns the username.
   *
   * @return the username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the email address.
   *
   * @return the email address.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the user's first name.
   *
   * @return the first name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the user's surname.
   *
   * @return the surname.
   */
  public String getSurname() {
    return surname;
  }

  /**
   * Returns the user's phone number.
   *
   * @return the phone number.
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Returns the user role.
   *
   * @return the user role.
   */
  public UserRole getRole() {
    return role;
  }

  /**
   * Returns the plain-text password, when present.
   *
   * @return the plain-text password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the hashed password, when present.
   *
   * @return the hashed password.
   */
  public String getPasswordHash() {
    return passwordHash;
  }

  /**
   * Returns the creation timestamp.
   *
   * @return the creation timestamp.
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Returns the last update timestamp.
   *
   * @return the last update timestamp.
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Builder for {@link User}. All fields are optional, but typically only a subset will be
   * populated depending on the use case (e.g. registration input vs DB read).
   *
   * @author SWAD Team
   */
  public static final class Builder {

    private Integer id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private UserRole role;
    private String password;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Sets the id. Typically only used when building a User object from a DB read, not for
     * registration input.
     *
     * @param id the id to set
     * @return the builder for chaining
     */
    public Builder id(final Integer id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the username. Required for both registration and DB read.
     *
     * @param username the username to set
     * @return the builder for chaining
     */
    public Builder username(final String username) {
      this.username = username;
      return this;
    }

    /**
     * Sets the email. Required for both registration and DB read.
     *
     * @param email the email to set
     * @return the builder for chaining
     */
    public Builder email(final String email) {
      this.email = email;
      return this;
    }

    /**
     * Sets the name. Required for both registration and DB read.
     *
     * @param name the name to set
     * @return the builder for chaining
     */
    public Builder name(final String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the surname. Required for both registration and DB read.
     *
     * @param surname the surname to set
     * @return the builder for chaining
     */
    public Builder surname(final String surname) {
      this.surname = surname;
      return this;
    }

    /**
     * Sets the phone number. Required for both registration and DB read.
     *
     * @param phoneNumber the phone number to set
     * @return the builder for chaining
     */
    public Builder phoneNumber(final String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    /**
     * Sets the user role. Required for both registration and DB read.
     *
     * @param role the user role to set
     * @return the builder for chaining
     */
    public Builder role(final UserRole role) {
      this.role = role;
      return this;
    }

    /**
     * Sets the plain-text password. Only used for registration input; should not be set when
     * building a User from a DB read.
     *
     * @param password the plain-text password to set
     * @return the builder for chaining
     */
    public Builder password(final String password) {
      this.password = password;
      return this;
    }

    /**
     * Sets the password hash. Only used when building a User from a DB read; should not be set for
     * registration input.
     *
     * @param passwordHash the password hash to set
     * @return the builder for chaining
     */
    public Builder passwordHash(final String passwordHash) {
      this.passwordHash = passwordHash;
      return this;
    }

    /**
     * Sets the creation timestamp. Only used when building a User from a DB read; should not be set
     * for registration input.
     *
     * @param createdAt the creation timestamp to set
     * @return the builder for chaining
     */
    public Builder createdAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    /**
     * Sets the update timestamp. Only used when building a User from a DB read; should not be set
     * for registration input.
     *
     * @param updatedAt the update timestamp to set
     * @return the builder for chaining
     */
    public Builder updatedAt(final LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    /**
     * Builds the User object. No validation is performed, so it's the caller's responsibility to
     * ensure that the appropriate fields are set for the intended use case (e.g. registration vs DB
     * read).
     *
     * @return a new User object with the fields set in the builder
     */
    public User build() {
      return new User(this);
    }
  }

  @Override
  protected void writeJSON(final OutputStream out) throws Exception {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    // Password nor passwordHash should ever be serialized to JSON, so we intentionally omit them
    // here.

    jg.writeStartObject();

    if (id != null) jg.writeNumberField("id", id);

    if (username != null) jg.writeStringField("username", username);

    if (email != null) jg.writeStringField("email", email);

    if (name != null) jg.writeStringField("name", name);

    if (surname != null) jg.writeStringField("surname", surname);
    if (phoneNumber != null) jg.writeStringField("phone_number", phoneNumber);

    if (role != null) jg.writeStringField("role", role.name());

    if (createdAt != null) {
      jg.writeStringField("created_at", createdAt.toString());
    }

    if (updatedAt != null) {
      jg.writeStringField("updated_at", updatedAt.toString());
    }

    jg.writeEndObject();

    jg.flush();
  }

  /**
   * Parses a {@code User} from a JSON request body:
   *
   * @param in the input stream containing the JSON payload.
   * @return a new {@code User} with a plain-text password.
   * @throws IOException if there is an error reading from the stream or parsing the JSON.
   * @throws UnexpectedKeyException if the payload contains unsupported fields.
   */
  public static User fromJSON(final InputStream in) throws IOException, UnexpectedKeyException {
    final Builder b = new Builder();

    final JsonParser jp = JSON_FACTORY.createParser(in);

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
        continue;
      }

      switch (jp.currentName()) {
        case "id":
          jp.nextToken();
          b.id(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue());
          break;
        case "username":
          jp.nextToken();
          b.username(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText());
          break;
        case "email":
          jp.nextToken();
          b.email(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText());
          break;
        case "name":
          jp.nextToken();
          b.name(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText());
          break;
        case "surname":
          jp.nextToken();
          b.surname(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText());
          break;
        case "phone_number":
          jp.nextToken();
          b.phoneNumber(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText());
          break;
        case "role":
          jp.nextToken();
          b.role(
              jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : UserRole.valueOf(jp.getText()));
          break;
        case "password":
          jp.nextToken();
          b.password(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText());
          break;
        default:
          throw new UnexpectedKeyException("Unexpected key in JSON: " + jp.currentName());
      }
    }

    return b.build();
  }
}
