package com.swad.taptable.rest.user;

import com.swad.taptable.dao.AuthenticateUserDAO;
import com.swad.taptable.exception.MalformedJSONException;
import com.swad.taptable.resources.Credentials;
import com.swad.taptable.resources.Message;
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

        final Credentials credentials;

        // Deserialization of credentials from request body. We catch both IOException (for general
        // I/O
        // issues) and MalformedJSONException (for JSON parsing issues) to provide precise error
        // handling and messaging.
        try {
            credentials = Credentials.fromJSON(req.getInputStream());
        } catch (IOException ex) {
            LOGGER.error("Malformed JSON in login request: %s", ex);
            new Message("Malformed request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
                    ex.getMessage()).toJSON(res.getOutputStream());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            return;
        } catch (MalformedJSONException ex) {
            LOGGER.error("Malformed JSON in login request: %s", ex.getMessage());
            new Message("Malformed request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
                    ex.getMessage()).toJSON(res.getOutputStream());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            return;
        }

        if (isMissing(credentials.getEmail()) || isMissing(credentials.getPassword())) {
            LOGGER.error("Login request missing email or password.");
            new Message("Missing email or password.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
                    "Fields 'email' and 'password' are required.").toJSON(res.getOutputStream());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            return;
        }

        final Integer userId;
        try {
            userId = new AuthenticateUserDAO(credentials.getEmail(), credentials.getPassword())
                    .access().getOutputParam();
        } catch (SQLException ex) {
            // AbstractDAO already logged the full stack trace; just add request-level context here.
            LOGGER.warn("Unexpected DB error during login for email '%s'.", credentials.getEmail());
            new Message("Login failed: database error.", ErrorCodes.UNEXPECTED_DB_ERROR,
                    ex.getMessage()).toJSON(res.getOutputStream());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            return;
        }

        if (userId == null) {
            LOGGER.warn("Failed login attempt for email '%s'.", credentials.getEmail());
            new Message("Invalid email or password.", ErrorCodes.INVALID_CREDENTIALS, null)
                    .toJSON(res.getOutputStream());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            return;
        }

        // --- 4. Issue JWT ---
        final String jwt = JWTUtil.generateToken(userId);

        // TODO: add Secure flag when running over HTTPS in production.
        res.addHeader("Set-Cookie", JWTUtil.COOKIE_NAME + "=" + jwt
                + "; Path=/; HttpOnly; SameSite=Strict; Max-Age=" + JWTUtil.EXPIRY_SECONDS);

        LOGGER.info("User %d logged in successfully.", userId);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    private static boolean isMissing(final String s) {
        return s == null || s.isBlank();
    }
}
