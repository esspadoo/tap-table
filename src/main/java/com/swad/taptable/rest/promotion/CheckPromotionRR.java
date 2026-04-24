package com.swad.taptable.rest.promotion;

import com.swad.taptable.dao.promotions.CheckPromotionDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Promotion;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class CheckPromotionRR extends AbstractRR {
  /**
   * Creates a new REST resource.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public CheckPromotionRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.CHECK_PROMOTION, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    // Code is expected to be passed as a path parameter
    final String code = (String) req.getAttribute("promotion_code");

    try {
      final CheckPromotionDAO dao = new CheckPromotionDAO(code);

      final Promotion promotion = dao.access().getOutputParam();

      if (promotion == null) {
        LOGGER.warn("Promotion with code %s not found.", code);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m = new Message("Promotion with code " + code + " not found.",
            ErrorCodes.RESOURCE_NOT_FOUND, null);
        res.setContentType(JSON_UTF_8_MEDIA_TYPE);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Promotion with code %s successfully retrieved.", promotion.getCode());
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      promotion.toJSON(res.getOutputStream());
    } catch (SQLException ex) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Unexpected database error: no. " + ex.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      m.toJSON(res.getOutputStream());
    }
  }
}
