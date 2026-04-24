package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.EditUserDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
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
 * REST resource that handles editing of an existing user
 *
 * @author SWAD Team
 */
public class EditUserRR extends AbstractRR {

  /**
   * Creates the REST resource that updates a user profile.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public EditUserRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.EDIT_USER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int userId = Integer.parseInt((String) req.getAttribute("user_id"));
      final User in = User.fromJSON(req.getInputStream());

      if (in.getName() == null || in.getSurname() == null || in.getEmail() == null
          || in.getPhoneNumber() == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message(
            "Missing required fields: name, surname, email and phone_number must be provided.",
            ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final User out = new EditUserDAO(userId, in).access().getOutputParam();

      if (out == null) {
        LOGGER.warn("User with id %d not found.", userId);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m = new Message(String.format("User with id %d not found.", userId),
            ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("User with id %d updated successfully.", userId);
      res.setStatus(HttpServletResponse.SC_OK);
      out.toJSON(res.getOutputStream());

    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed JSON in request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      if ("23505".equals(e.getSQLState())) {
        LOGGER.warn("Update conflict: email or phone number already in use.");
        res.setStatus(HttpServletResponse.SC_CONFLICT);
        Message m = new Message("Email or phone number already in use.",
            ErrorCodes.RESOURCE_ALREADY_EXISTS, null);
        m.toJSON(res.getOutputStream());
      } else {
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Message m = new Message("Database error while updating user.",
            ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
        m.toJSON(res.getOutputStream());
      }
    }
  }
}
