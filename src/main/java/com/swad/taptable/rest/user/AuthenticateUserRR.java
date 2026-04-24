package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.AuthenticateUserDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Credentials;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.User;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import com.swad.taptable.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles user authentication (login). Expects a JSON body with {@code email} and {@code password}
 * fields, and returns a JWT in an HttpOnly cookie on successful authentication.
 *
 * <p>
 * Example request body:
 *
 * <pre>
 * { "email": "mario@test.com", "password": "supersecret" }
 * </pre>
 */
public final class AuthenticateUserRR extends AbstractRR {

  public AuthenticateUserRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.AUTHENTICATE_USER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Credentials credentials = Credentials.fromJSON(req.getInputStream());

      if (isMissing(credentials.getEmail()) || isMissing(credentials.getPassword())) {
        LOGGER.warn("Login request missing email or password.");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Missing email or password.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
            "Fields 'email' and 'password' are required.");
        m.toJSON(res.getOutputStream());
        return;
      }

      final User user = new AuthenticateUserDAO(credentials.getEmail(), credentials.getPassword())
          .access().getOutputParam();

      if (user == null) {
        LOGGER.warn("Failed login attempt for email '%s'.", credentials.getEmail());
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Message m = new Message("Invalid email or password.", ErrorCodes.INVALID_CREDENTIALS, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final String jwt = JWTUtil.generateToken(user.getId(), user.getRole());

      res.addHeader("Set-Cookie", JWTUtil.COOKIE_NAME + "=" + jwt
          + "; Path=/; HttpOnly; SameSite=Strict; Max-Age=" + JWTUtil.EXPIRY_SECONDS);

      LOGGER.info("User %d logged in successfully.", user.getId());
      res.setStatus(HttpServletResponse.SC_OK);

    } catch (UnexpectedKeyException e) {
      LOGGER.error("Unexpected key in JSON login request: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (IOException e) {
      LOGGER.error("Failed to parse JSON login request: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed JSON in request body.", ErrorCodes.INVALID_INPUT_PARAMETER,
          e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (SQLException ex) {
      LOGGER.warn("Unexpected DB error during login.");
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Login failed: database error.", ErrorCodes.UNEXPECTED_DB_ERROR,
          ex.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }

  private static boolean isMissing(final String s) {
    return s == null || s.isBlank();
  }
}
