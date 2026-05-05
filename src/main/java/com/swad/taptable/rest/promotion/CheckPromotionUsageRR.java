/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.promotion;

import com.swad.taptable.dao.promotions.CheckPromotionUsageDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.PromotionUsageFlag;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles checking if a promotion code has already been used by a user
 *
 * @author SWAD Team
 */
public class CheckPromotionUsageRR extends AbstractRR {

  /**
   * Creates the REST resource that checks promotion usage for a user.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public CheckPromotionUsageRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.CHECK_PROMOTION_USAGE, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      // Promotion code is expected to be passed as a path parameter
      final String promotionCode = (String) req.getAttribute("promotion_code");

      // User
      final int userId = Integer.parseInt((String) req.getAttribute("user_id"));

      final CheckPromotionUsageDAO dao = new CheckPromotionUsageDAO(userId, promotionCode);
      final PromotionUsageFlag isPromotionAlreadyUsed = dao.access().getOutputParam();

      if (isPromotionAlreadyUsed.isUsed()) {
        LOGGER.info(
            "Promotion with code %s has already been used by user with id %d.",
            promotionCode, userId);
      } else {
        LOGGER.info(
            "Promotion with code %s has not been used by user with id %d.", promotionCode, userId);
      }

      res.setStatus(HttpServletResponse.SC_OK);
      isPromotionAlreadyUsed.toJSON(res.getOutputStream());

    } catch (SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m =
          new Message("Unexpected database error", ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
