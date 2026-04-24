package com.swad.taptable.rest.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swad.taptable.dao.order.CreateOrderDAO;
import com.swad.taptable.exception.NotValidOrderException;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Order;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

/**
 * REST resource that handles creation of a new order
 *
 * @author SWAD Team
 */
public class NewOrderRR extends AbstractRR {
  /**
   * Creates the REST resource that handles order creation.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public NewOrderRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.NEW_ORDER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final Integer userId = Integer.parseInt((String) req.getAttribute("user_id"));
      Order in = Order.fromJSON(req.getInputStream());

      if (in.getStatus() == null) {
        LOGGER.warn("Invalid status in order %s", in.getStatus());
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m =
            new Message("invalid status in order.", ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      if (in.getDishes() == null || in.getDishes().isEmpty()) {
        LOGGER.warn("Order with no items.");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Order must contain at least one item",
            ErrorCodes.INVALID_INPUT_PARAMETER, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final CreateOrderDAO dao = new CreateOrderDAO(in, userId);
      final Order out = dao.access().getOutputParam();

      if (out == null) {
        LOGGER.warn("Failed to create order for user %d", userId);
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Message m = new Message("Failed to create order. Please check the provided data.",
            ErrorCodes.WRONG_RESOURCE_PROVIDED, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Order with id %d has been created", out.getId());

      res.setStatus(HttpServletResponse.SC_CREATED);
      out.toJSON(res.getOutputStream());
    } catch (UnexpectedKeyException | MalformedURLException | JsonProcessingException e) {
      LOGGER.error("Malformed JSON Exception", e);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Malformed JSON in request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
          e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (NotValidOrderException e) {
      LOGGER.error("Invalid order", e);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Invalid order.", ErrorCodes.WRONG_RESOURCE_PROVIDED, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      Message m = new Message("Unexpected database error: no. " + e.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      m.toJSON(res.getOutputStream());
    }
  }
}
