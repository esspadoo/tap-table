package com.swad.taptable.rest.order;

import com.swad.taptable.dao.order.GetOrderDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Order;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles retrieval of a single order by ID
 *
 * @author SWAD Team
 */
public class GetOrderRR extends AbstractRR {
  /**
   * Creates the REST resource that retrieves a single order.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public GetOrderRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.GET_ORDER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    final int orderId;

    try {
      orderId = Integer.parseInt((String) req.getAttribute("order_id"));
      try {
        final GetOrderDAO getOrderDAO = new GetOrderDAO(orderId);
        Order order = getOrderDAO.access().getOutputParam();
        if (order != null) {
          LOGGER.info("Order with id %d successfully retrieved.", orderId);
          res.setStatus(HttpServletResponse.SC_OK);
          order.toJSON(res.getOutputStream());
        } else {
          LOGGER.warn("Order with id %d not found.", orderId);
          Message m = new Message("Order with id " + orderId + " not found.",
              ErrorCodes.RESOURCE_NOT_FOUND, null);
          res.setStatus(HttpServletResponse.SC_NOT_FOUND);
          m.toJSON(res.getOutputStream());
        }
      } catch (SQLException ex) {
        LOGGER.error("Database error (no. %d) while retrieving order with code %s: %s",
            ex.getErrorCode(), orderId, ex.getMessage());
        Message m = new Message("Unexpected database error: no. " + ex.getErrorCode(),
            ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        m.toJSON(res.getOutputStream());
      }
    } catch (NumberFormatException e) {
      LOGGER.warn("Invalid order ID: " + req.getAttribute("order_id"));
      Message m = new Message("Invalid order ID " + req.getAttribute("order_id"),
          ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      m.toJSON(res.getOutputStream());
    }
  }
}
