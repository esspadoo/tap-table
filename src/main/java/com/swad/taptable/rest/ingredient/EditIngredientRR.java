package com.swad.taptable.rest.ingredient;

import java.io.IOException;
import java.sql.SQLException;
import com.swad.taptable.dao.ingredient.EditIngredientDAO;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * REST resource that handles editing of an existing ingredient
 *
 * @author SWAD Team
 */
public class EditIngredientRR extends AbstractRR {

  /**
   * Creates the REST resource that updates an ingredient.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public EditIngredientRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.EDIT_INGREDIENT, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Ingredient in = Ingredient.fromJSON(req.getInputStream());

      /**
       * Check that the required fields provided are not null nor empty. The id is required to
       * identify the ingredient to edit, while name and isFrozen are required by the DAO (and the
       * DB as the value cannot be null) to update the ingredient.
       */
      if (in.getId() == null || in.getName() == null || in.isFrozen() == null
          || in.getName().isBlank()) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Missing required fields: name and isFrozen must be provided",
            ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());

        // Return early since the input is not valid
        return;
      }

      // If the input is valid, proceed with the edit operation
      EditIngredientDAO dao = new EditIngredientDAO(in);
      Ingredient out = dao.access().getOutputParam();

      if (out == null) {
        LOGGER.warn("Ingredient with id %d not found for editing.", in.getId());
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m = new Message("Ingredient with id " + in.getId() + " not found.",
            ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_OK);
      out.toJSON(res.getOutputStream());

    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);

      Message m = new Message("Unexpected key in JSON: " + e.getMessage(),
          ErrorCodes.WRONG_RESOURCE_PROVIDED, null);
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Database error while editing ingredient.",
          ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
