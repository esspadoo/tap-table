package com.swad.taptable.rest.ingredient;

import java.io.IOException;
import java.sql.SQLException;
import com.swad.taptable.dao.ingredient.DeleteIngredientDAO;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * REST resource for deleting an ingredient given its id.
 * 
 * @author SWAD Team
 */
public class DeleteIngredientRR extends AbstractRR {
  /**
   * Creates a new {@code DeleteIngredientRR} object.
   * 
   * @param req the HTTP request
   * @param res the HTTP response
   */
  public DeleteIngredientRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.DELETE_INGREDIENT, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int ingredientId = Integer.parseInt((String) req.getAttribute("ingredient_id"));

      try {
        final DeleteIngredientDAO dao = new DeleteIngredientDAO(ingredientId);

        final Ingredient deletedIngredient = dao.access().getOutputParam();

        // Check if the ingredient was not deleted
        if (deletedIngredient == null) {

          // Log the case when the ingredient with the specified id is not found
          LOGGER.warn("Ingredient with id %d not found.", ingredientId);
          Message m = new Message(String.format("Ingredient with id %d not found", ingredientId),
              ErrorCodes.RESOURCE_NOT_FOUND, null);
          res.setStatus(HttpServletResponse.SC_NOT_FOUND);
          m.toJSON(res.getOutputStream());
          return;
        }

        // Log the successful deletion and return the deleted ingredient in the response
        LOGGER.info("Ingredient with id %d successfully deleted.", ingredientId);
        res.setStatus(HttpServletResponse.SC_OK);
        deletedIngredient.toJSON(res.getOutputStream());

      } catch (final SQLException ex) {
        // FIXME: check the code for known error that could happen and handle them properly
        // SQLException is already logged by the DAO
        Message m = new Message("Unexpected database error: no. " + ex.getErrorCode(),
            ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        m.toJSON(res.getOutputStream());
      }
    } catch (final NumberFormatException ex) {
      // Log the exception and return an error message with the appropriate error code
      LOGGER.error("Invalid ingredient id format: %s", req.getPathInfo());
      Message m = new Message("Invalid ingredient id format: " + req.getPathInfo(),
          ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      m.toJSON(res.getOutputStream());
    }
  }
}
