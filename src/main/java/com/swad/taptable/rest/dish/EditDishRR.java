/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.EditDishDAO;
import com.swad.taptable.exception.NotValidDishException;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles editing of an existing dish
 *
 * @author SWAD Team
 */
public class EditDishRR extends AbstractRR {

  /**
   * Creates the REST resource that updates a dish.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public EditDishRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.EDIT_DISH, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Dish in = Dish.fromJSON(req.getInputStream());

      if (in.getName() == null
          || in.getName().isBlank()
          || in.getPrice() == null
          || in.getIngredientIds() == null
          || in.getIngredientIds().isEmpty()
          || in.getCategory() == null
          || in.getId() == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m =
            new Message("Missing required fields", ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final Dish out = new EditDishDAO(in).access().getOutputParam();

      if (out == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m =
            new Message(
                "Dish with id " + in.getId() + " not found.", ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_OK);
      out.toJSON(res.getOutputStream());

    } catch (final NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message("Invalid dish id format", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final NotValidDishException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Invalid dish data: " + e.getMessage(), ErrorCodes.INVALID_INPUT_PARAMETER, null);
      m.toJSON(res.getOutputStream());
    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Malformed JSON in request body.",
              ErrorCodes.WRONG_RESOURCE_PROVIDED,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message(
              "Database error while editing dish.", ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
