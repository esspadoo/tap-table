/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.ChangeUserRoleDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.RoleChange;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ChangeUserRoleRR extends AbstractRR {

  public ChangeUserRoleRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.CHANGE_USER_ROLE, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int targetUserId = Integer.parseInt((String) req.getAttribute("user_id"));
      final RoleChange payload = RoleChange.fromJSON(req.getInputStream());

      if (payload.getRole() == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Message m =
            new Message("Missing required field: role.", ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      if (payload.getRole() == UserRole.ADMIN) {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        final Message m =
            new Message("Cannot assign ADMIN role.", ErrorCodes.FORBIDDEN_OPERATION, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final User updated =
          new ChangeUserRoleDAO(targetUserId, payload.getRole()).access().getOutputParam();

      if (updated == null) {
        LOGGER.warn("User %d not found or is an ADMIN - role change rejected.", targetUserId);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        final Message m =
            new Message(
                String.format("User %d not found.", targetUserId),
                ErrorCodes.RESOURCE_NOT_FOUND,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Role of user %d changed to %s.", targetUserId, payload.getRole());
      res.setStatus(HttpServletResponse.SC_OK);
      updated.toJSON(res.getOutputStream());

    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message(
              "Malformed JSON in request body.",
              ErrorCodes.WRONG_RESOURCE_PROVIDED,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message(
              "Invalid user ID: " + req.getAttribute("user_id"),
              ErrorCodes.INVALID_INPUT_PARAMETER,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final IllegalArgumentException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message("Invalid role value.", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      LOGGER.error("Database error while changing user role: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      final Message m =
          new Message(
              "Database error while changing user role.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
