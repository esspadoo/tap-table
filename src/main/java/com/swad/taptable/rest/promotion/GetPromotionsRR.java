/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.promotion;

import com.swad.taptable.dao.promotions.GetPromotionsDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Promotion;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles retrieval of all promotions
 *
 * @author SWAD Team
 */
public class GetPromotionsRR extends AbstractRR {

  /**
   * Creates the REST resource that retrieves all promotions.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public GetPromotionsRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.GET_PROMOTIONS, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      GetPromotionsDAO dao = new GetPromotionsDAO();
      final ResourceList<Promotion> promotions = dao.access().getOutputParam();

      if (promotions == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m = new Message("No promotions found.", ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_OK);
      promotions.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message(
              "Unexpected database error: no. " + e.getErrorCode(),
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
