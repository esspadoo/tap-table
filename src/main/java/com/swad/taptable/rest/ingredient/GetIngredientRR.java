/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.ingredient;

import com.swad.taptable.dao.ingredient.GetIngredientDAO;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Rest resource for retrieving an ingredient given its id.
 *
 * <p>The {@code id}, passed as a path parameter, is used to identify the ingredient to be
 * retrieved. If the id provided is numerical only (alphanumerical), the response status is set to
 * {@code 400} (Bad Request).
 *
 * <p>If the ingredient is successfully retrieved, the response status is set to {@code 200} (OK)
 * and the ingredient is returned in the response body as a JSON object.
 *
 * <p>If the ingredient with the specified id does not exist, the response status is set to {@code
 * 404} (Not Found).
 *
 * <p>If any server side error occurs (e.g. database) during the retrieval process, the response
 * status is set to {@code 500} (Internal Server Error).
 *
 * @author SWAD Team
 */
public class GetIngredientRR extends AbstractRR {

  /**
   * Creates a new {@code GetIngredientRR} object.
   *
   * @param req the HTTP request
   * @param res the HTTP response
   */
  public GetIngredientRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.GET_INGREDIENT, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int ingredientId = Integer.parseInt((String) req.getAttribute("ingredient_id"));

      GetIngredientDAO dao = new GetIngredientDAO(ingredientId);
      final Ingredient ingredient = dao.access().getOutputParam();

      // Check if the ingredient was not found
      if (ingredient == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m =
            new Message(
                String.format("Ingredient %d not found in database", ingredientId),
                ErrorCodes.RESOURCE_NOT_FOUND,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      // Log the successful retrieval and return the ingredient in the response
      res.setStatus(HttpServletResponse.SC_OK);
      ingredient.toJSON(res.getOutputStream());

    } catch (final SQLException e) {
      // FIXME: check the code for known error that could happen and handle them properly
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message(
              "Database error while retrieving ingredient", ErrorCodes.UNEXPECTED_DB_ERROR, null);
      m.toJSON(res.getOutputStream());
    } catch (final NumberFormatException e) {
      LOGGER.error("Ingredient id is not a valid integer: " + req.getAttribute("ingredient_id"), e);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message(
              "Ingredient id must be a valid integer", ErrorCodes.INVALID_INPUT_PARAMETER, null);
      m.toJSON(res.getOutputStream());
    }
  }
}
