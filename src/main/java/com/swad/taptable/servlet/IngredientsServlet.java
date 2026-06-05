/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.ingredient.GetIngredientsDAO;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Servlet handling the ingredients page, allowing users to view the list of available ingredients
 * in the system.
 *
 * @author SWAD Team
 */
public final class IngredientsServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(IngredientsServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_INGREDIENTS);

    try {
      ResourceList<Ingredient> ingredients = new GetIngredientsDAO().access().getOutputParam();
      req.setAttribute("ingredients", ingredients.getList());

      LOGGER.debug("Serving ingredients page.");
      req.getRequestDispatcher("/jsp/ingredients.jsp").forward(req, res);
    } catch (Exception e) {
      LOGGER.error("Error serving ingredients page.", e);
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }
}
