/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.DeleteUserDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.User;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles deletion of a user identified by {@code id}
 *
 * @author SWAD Team
 */
public class DeleteUserRR extends AbstractRR {

  /**
   * Creates the REST resource that deletes a user.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public DeleteUserRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.DELETE_USER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int userId = Integer.parseInt((String) req.getAttribute("user_id"));

      final User deleted = new DeleteUserDAO(userId).access().getOutputParam();

      if (deleted == null) {
        LOGGER.warn("User with id %d not found.", userId);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        new Message(
                String.format("User with id %d not found.", userId),
                ErrorCodes.RESOURCE_NOT_FOUND,
                null)
            .toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("User with id %d deleted successfully.", userId);
      res.setStatus(HttpServletResponse.SC_OK);
      deleted.toJSON(res.getOutputStream());

    } catch (final NumberFormatException e) {
      LOGGER.warn("Invalid user ID format: %s", req.getAttribute("user_id"));
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Invalid user ID: " + req.getAttribute("user_id"),
              ErrorCodes.INVALID_INPUT_PARAMETER,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message(
              "Database error while deleting user.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
