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
 * On success, responds with HTTP 201 Created and a JSON body containing the new user's ID. On
 * failure, responds with an appropriate HTTP status code and a JSON body containing an error
 * message and code.
 * </p>
 *
 * @author SWAD Team
 */
public final class RegisterUserRR extends AbstractRR {

  /**
   * Creates the REST resource that handles user registration.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public RegisterUserRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.REGISTER_USER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final User user = User.fromJSON(req.getInputStream());

      if (isMissing(user.getUsername()) || isMissing(user.getEmail())
          || isMissing(user.getPassword()) || isMissing(user.getName())
          || isMissing(user.getSurname()) || isMissing(user.getPhoneNumber())) {
        LOGGER.warn("Registration request missing required fields.");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Missing required fields.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
            "Fields 'username', 'email', 'password', 'name', 'surname', and 'phone_number' are required.");
        m.toJSON(res.getOutputStream());
        return;
      }

      if (!Validator.isValidEmail(user.getEmail())) {
        LOGGER.warn("Registration request contains invalid email: '%s'.", user.getEmail());
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Invalid email address.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
            "The provided email address is not valid.");
        m.toJSON(res.getOutputStream());
        return;
      }

      if (!Validator.isValidPassword(user.getPassword())) {
        LOGGER.warn(
            "Registration request contains a password that does not meet policy requirements.");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Invalid password.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
            "Password must be 8-16 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        m.toJSON(res.getOutputStream());
        return;
      }

      final Integer userId = new RegisterUserDAO(user).access().getOutputParam();

      LOGGER.info("User %d registered successfully.", userId);
      res.setStatus(HttpServletResponse.SC_CREATED);
      new User.Builder().id(userId).build().toJSON(res.getOutputStream());

    } catch (UnexpectedKeyException | IOException e) {
      LOGGER.warn("Malformed JSON in registration request: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (SQLException ex) {
      if ("23505".equals(ex.getSQLState())) {
        LOGGER.warn("Registration conflict: email, username, or phone number already in use.");
        res.setStatus(HttpServletResponse.SC_CONFLICT);
        Message m = new Message("Email, username, or phone number already in use.",
            ErrorCodes.RESOURCE_ALREADY_EXISTS, null);
        m.toJSON(res.getOutputStream());
      } else {
        LOGGER.warn("Unexpected DB error during registration for username.");
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Message m = new Message("Registration failed: database error.",
            ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
        m.toJSON(res.getOutputStream());
      }
    }
  }

  /**
   * Method that checks if a string is missing (null or blank)
   *
   * @param s the string to validate.
   * @return {@code true} if the string is {@code null} or blank, {@code false} otherwise.
   */
  private static boolean isMissing(final String s) {
    return s == null || s.isBlank();
  }
}
