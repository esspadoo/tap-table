package com.swad.taptable.rest.order;

import com.swad.taptable.dao.order.GetUserOrdersDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles retrieval of all orders for a specific user
 *
 * @author SWAD Team
 */
public class GetUserOrdersRR extends AbstractRR {
  /**
   * Creates the REST resource that retrieves all orders for a specific user.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public GetUserOrdersRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.GET_USER_ORDERS, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      int userId = Integer.parseInt((String) req.getAttribute("user_id"));

      final GetUserOrdersDAO dao = new GetUserOrdersDAO(userId);
      final ResourceList<Order> orders = dao.access().getOutputParam();

      if (orders == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m =
            new Message("No orders found for user " + userId, ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      res.setStatus(HttpServletResponse.SC_OK);
      orders.toJSON(res.getOutputStream());

    } catch (NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Invalid user ID: must be a number.",
          ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      LOGGER.error("Database error (no. %d) while retrieving orders for user: %s", e.getErrorCode(),
          e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Unexpected database error: no. " + e.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
