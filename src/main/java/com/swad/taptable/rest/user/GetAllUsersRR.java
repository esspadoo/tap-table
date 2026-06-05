/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.GetAllUsersDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.resources.User;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class GetAllUsersRR extends AbstractRR {

  public GetAllUsersRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.GET_ALL_USERS, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final ResourceList<User> users = new GetAllUsersDAO().access().getOutputParam();
      LOGGER.info("Retrieved all users successfully.");
      res.setStatus(HttpServletResponse.SC_OK);
      users.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      LOGGER.error("Database error while retrieving users: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      final Message m =
          new Message(
              "Database error while retrieving users.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
