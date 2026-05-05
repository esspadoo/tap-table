/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.ingredient;

import com.swad.taptable.dao.ingredient.GetIngredientsDAO;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles retrieval of all ingredients in the system
 *
 * @author SWAD Team
 */
public class GetIngredientsRR extends AbstractRR {

  /**
   * Creates the REST resource that retrieves all ingredients.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public GetIngredientsRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.GET_INGREDIENTS, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    ResourceList<Ingredient> ingredients = null;
    try {
      GetIngredientsDAO dao = new GetIngredientsDAO();
      ingredients = dao.access().getOutputParam();
      res.setStatus(HttpServletResponse.SC_OK);
      ingredients.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      // FIXME: check the code for known error that could happen and handle them properly
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message(
              "Database error while retrieving ingredients", ErrorCodes.UNEXPECTED_DB_ERROR, null);
      m.toJSON(res.getOutputStream());
    }
  }
}
