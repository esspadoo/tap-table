package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.RegisterUserDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.User;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import com.swad.taptable.util.Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * <p>
 * REST resource for user registration. Expects a JSON body with the following fields
 * </p>
 * 
 * <p>
 * Example request body:
 * 
 * <pre>
 * { "username": "johndoe", "email": "john@test.com", "password": "password123", "name": "John",
 * "surname": "Doe", "phone_number": "1234567890" }
 * </pre>
 * </p>
 * 
 * <p>
 * On success, responds with HTTP 201 Created and a JSON body containing the new user's ID. On
 * failure, responds with an appropriate HTTP status code and a JSON body containing an error
 * message and code.
 * </p>
 * 
 * @author SWAD Team
 */
public final class RegisterUserRR extends AbstractRR {

  public RegisterUserRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.REGISTER_USER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    final User user;

    try {
      user = User.fromJSON(req.getInputStream());
    } catch (IOException e) {
      LOGGER.warn("Malformed JSON in registration request.", e);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          e.getMessage());
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      m.toJSON(res.getOutputStream());
      return;
    } catch (UnexpectedKeyException ex) {
      LOGGER.warn("Malformed JSON in registration request: %s", ex.getMessage());
      new Message("Malformed request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED, ex.getMessage())
          .toJSON(res.getOutputStream());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      return;
    }

    if (isMissing(user.getUsername()) || isMissing(user.getEmail()) || isMissing(user.getPassword())
        || isMissing(user.getName()) || isMissing(user.getSurname())
        || isMissing(user.getPhoneNumber())) {
      LOGGER.warn("Registration request missing required fields.");
      new Message("Missing required fields.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          "Fields 'username', 'email', 'password', 'name', 'surname', and 'phone_number' are required.")
              .toJSON(res.getOutputStream());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      return;
    }

    if (!Validator.isValidEmail(user.getEmail())) {
      LOGGER.warn("Registration request contains invalid email: '%s'.", user.getEmail());
      new Message("Invalid email address.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          "The provided email address is not valid.").toJSON(res.getOutputStream());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      return;
    }

    if (!Validator.isValidPassword(user.getPassword())) {
      LOGGER
          .warn("Registration request contains a password that does not meet policy requirements.");
      new Message("Invalid password.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          "Password must be 8-16 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
              .toJSON(res.getOutputStream());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      return;
    }

    final Integer userId;
    try {
      userId = new RegisterUserDAO(user).access().getOutputParam();
    } catch (SQLException ex) {
      if ("23505".equals(ex.getSQLState())) {
        LOGGER.warn("Registration conflict: email, username, or phone number already in use.");
        new Message("Email, username, or phone number already in use.",
            ErrorCodes.RESOURCE_ALREADY_EXISTS, null).toJSON(res.getOutputStream());
        res.setStatus(HttpServletResponse.SC_CONFLICT);
      } else {
        // AbstractDAO already logged the full stack trace; just add request-level context here.
        LOGGER.warn("Unexpected DB error during registration for username '%s'.",
            user.getUsername());
        new Message("Registration failed: database error.", ErrorCodes.UNEXPECTED_DB_ERROR,
            ex.getMessage()).toJSON(res.getOutputStream());
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      return;
    }

    LOGGER.info("User %d registered successfully.", userId);
    res.setStatus(HttpServletResponse.SC_CREATED);
    res.setContentType(JSON_UTF_8_MEDIA_TYPE);
    new User.Builder().id(userId).build().toJSON(res.getOutputStream());
  }

  private static boolean isMissing(final String s) {
    return s == null || s.isBlank();
  }
}
