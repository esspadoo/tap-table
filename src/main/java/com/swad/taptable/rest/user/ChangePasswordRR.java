/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.user;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.dao.user.ChangePasswordDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ChangePasswordRR extends AbstractRR {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();

  public ChangePasswordRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.UPDATE_PASSWORD, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int userId = Integer.parseInt((String) req.getAttribute("user_id"));

      String currentPassword = null;
      String newPassword = null;
      try (JsonParser jp = JSON_FACTORY.createParser(req.getInputStream())) {
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
      }

      if (currentPassword == null || newPassword == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Message m =
            new Message(
                "Missing required fields: current_password and new_password.",
                ErrorCodes.INVALID_INPUT_PARAMETER,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      if (newPassword.length() < 8) {
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
          new ChangePasswordDAO(userId, currentPassword, newPassword).access().getOutputParam();

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
