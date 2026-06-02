/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.GetDishImageDAO;
import com.swad.taptable.resources.Image;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that serves the image of a dish.
 *
 * @author SWAD Team
 */
public class GetDishImageRR extends AbstractRR {

  /**
   * Creates the REST resource that retrieves a dish image
   *
   * @param req the HTTP request
   * @param res the HTTP response
   */
  public GetDishImageRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.GET_DISH_IMAGE, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int dishId = Integer.parseInt((String) req.getAttribute("dish_id"));

      final GetDishImageDAO dao = new GetDishImageDAO(dishId);
      dao.access();
      final Image image = dao.getOutputParam();

      if (image == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        final Message m =
            new Message("No image found for dish " + dishId, ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setContentType(image.getType().getMimeType());
      res.setContentLength(image.getBytes().length);
      res.setStatus(HttpServletResponse.SC_OK);
      res.getOutputStream().write(image.getBytes());

    } catch (final NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message(
              "Invalid dish id format.", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      final Message m =
          new Message(
              "Database error while retrieving dish image.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
