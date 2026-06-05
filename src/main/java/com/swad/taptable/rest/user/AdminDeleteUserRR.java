/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.AdminDeleteUserDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Rest resource for the deletion of a user by an admin in the system
 *
 * @author SWAD Team
 */
public class AdminDeleteUserRR extends AbstractRR {

  /**
   * Creates a new resource handler for the admin-delete-user action.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public AdminDeleteUserRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.ADMIN_DELETE_USER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int targetUserId = Integer.parseInt((String) req.getAttribute("user_id"));
      final boolean deleted = new AdminDeleteUserDAO(targetUserId).access().getOutputParam();

      if (!deleted) {
        LOGGER.warn("User %d not found or is an ADMIN - delete rejected.", targetUserId);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        final Message m =
            new Message(
                String.format("User %d not found.", targetUserId),
                ErrorCodes.RESOURCE_NOT_FOUND,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("User %d deleted by admin.", targetUserId);
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);

    } catch (final NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message(
              "Invalid user ID: " + req.getAttribute("user_id"),
              ErrorCodes.INVALID_INPUT_PARAMETER,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      LOGGER.error("Database error while deleting user: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      final Message m =
          new Message(
              "Database error while deleting user.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
