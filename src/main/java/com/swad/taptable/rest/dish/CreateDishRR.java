package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.CreateDishDAO;
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

public class CreateDishRR extends AbstractRR {

  public CreateDishRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.CREATE_DISH, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Dish in = Dish.fromJSON(req.getInputStream());

      if (in.getName() == null || in.getName().isBlank() || in.getPrice() == null || in.getPrice() <= 0
          || in.getIngredientIds() == null || in.getIngredientIds().isEmpty() || in.getCategory() == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Missing or invalid fields", ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final Dish out = new CreateDishDAO(in).access().getOutputParam();

      if (out == null) {
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Message m = new Message("Failed to create dish.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_CREATED);
      out.toJSON(res.getOutputStream());

    } catch (final NotValidDishException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Invalid dish data: " + e.getMessage(),
          ErrorCodes.INVALID_INPUT_PARAMETER, null);
      m.toJSON(res.getOutputStream());
    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed JSON in request body.",
          ErrorCodes.WRONG_RESOURCE_PROVIDED, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Database error while creating dish.",
          ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
