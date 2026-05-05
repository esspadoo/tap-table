/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.category;

import com.swad.taptable.dao.category.DeleteCategoryDAO;
import com.swad.taptable.resources.Category;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles deletion of a category by its name
 *
 * @author SWAD Team
 */
public class DeleteCategoryRR extends AbstractRR {

  /**
   * Creates the REST resource that deletes a category.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public DeleteCategoryRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.DELETE_CATEGORY, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final String categoryName = (String) req.getAttribute("category_name");

      final Category deleted = new DeleteCategoryDAO(categoryName).access().getOutputParam();

      if (deleted == null) {
        LOGGER.warn("Category '%s' not found.", categoryName);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        new Message(
                String.format("Category '%s' not found.", categoryName),
                ErrorCodes.RESOURCE_NOT_FOUND,
                null)
            .toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Category '%s' deleted.", deleted.getName());
      res.setStatus(HttpServletResponse.SC_OK);
      deleted.toJSON(res.getOutputStream());

    } catch (final SQLException e) {
      if ("23503".equals(e.getSQLState())) {
        LOGGER.warn("Cannot delete category: it is still referenced by existing dishes.");
        res.setStatus(HttpServletResponse.SC_CONFLICT);
        new Message(
                "Cannot delete category: it is referenced by existing dishes.",
                ErrorCodes.UNEXPECTED_DB_ERROR,
                e.getMessage())
            .toJSON(res.getOutputStream());
      } else {
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        new Message(
                "Database error while deleting category.",
                ErrorCodes.UNEXPECTED_DB_ERROR,
                e.getMessage())
            .toJSON(res.getOutputStream());
      }
    }
  }
}
