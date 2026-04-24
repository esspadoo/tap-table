package com.swad.taptable.rest.promotion;

import com.swad.taptable.dao.promotions.NewPromotionDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Promotion;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class NewPromotionRR extends AbstractRR {
  /**
   * Creates a new {@code NewPromotionRR} object.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public NewPromotionRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.NEW_PROMOTION, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Promotion in = Promotion.fromJSON(req.getInputStream());

      // Check if the required fields are present and valid
      if (in.getCode() == null || in.getCode().isBlank() || in.getType() == null
          || in.getType().isBlank() || in.getDescription() == null || in.getDescription().isBlank()
          || in.getValidFrom() == null || in.getValidTo() == null) {
        LOGGER.warn("Missing required fields: code, type or description is empty.");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m =
            new Message("Missing required fields", ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());

        // Return early since the input is not valid
        return;
      }

      // Check if the promotion timestamps are valid
      if (in.getValidTo().isBefore(in.getValidFrom())) {
        LOGGER.warn("Invalid promotion dates: validTo is before validFrom.");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Invalid promotion dates: validTo must be after validFrom",
            ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());

        // Return early since the promotion dates are not valid
        return;
      }

      final NewPromotionDAO dao = new NewPromotionDAO(in);

      final Promotion out = dao.access().getOutputParam();

      if (out == null) {
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Message m =
            new Message("Failed to create promotion.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_CREATED);
      out.toJSON(res.getOutputStream());
    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed JSON in request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException ex) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Unexpected database error: no. " + ex.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
