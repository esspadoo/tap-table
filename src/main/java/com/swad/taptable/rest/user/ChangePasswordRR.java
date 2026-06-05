/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.ChangePasswordDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.PasswordChange;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ChangePasswordRR extends AbstractRR {

  public ChangePasswordRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.UPDATE_PASSWORD, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int userId = Integer.parseInt((String) req.getAttribute("user_id"));
      final PasswordChange payload = PasswordChange.fromJSON(req.getInputStream());

      if (payload.getCurrentPassword() == null || payload.getNewPassword() == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Message m =
            new Message(
                "Missing required fields: current_password and new_password.",
                ErrorCodes.INVALID_INPUT_PARAMETER,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      if (payload.getNewPassword().length() < 8) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Message m =
            new Message(
                "new_password must be at least 8 characters.",
                ErrorCodes.INVALID_INPUT_PARAMETER,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final boolean ok =
          new ChangePasswordDAO(userId, payload.getCurrentPassword(), payload.getNewPassword())
              .access()
              .getOutputParam();

      if (!ok) {
        LOGGER.warn("Password change failed for user %d: wrong current password.", userId);
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final Message m =
            new Message("Current password is incorrect.", ErrorCodes.INVALID_CREDENTIALS, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Password changed successfully for user %d.", userId);
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);

    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message(
              "Malformed JSON in request body.",
              ErrorCodes.WRONG_RESOURCE_PROVIDED,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      LOGGER.error("Database error while changing password: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      final Message m =
          new Message(
              "Database error while changing password.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
