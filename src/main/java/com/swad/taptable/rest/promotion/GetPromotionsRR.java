package com.swad.taptable.rest.promotion;

import java.io.IOException;
import java.sql.SQLException;
import com.swad.taptable.dao.promotions.GetPromotionsDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Promotion;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetPromotionsRR extends AbstractRR {

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
        new Message("No promotions found.", ErrorCodes.RESOURCE_NOT_FOUND, null)
            .toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      promotions.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Unexpected database error: no. " + e.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      m.toJSON(res.getOutputStream());
    }
  }

}
