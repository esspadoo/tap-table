package com.swad.taptable.rest.order;

import com.swad.taptable.dao.order.GetAllOrdersDAO;
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
 * REST resource that handles retrieval of all orders in the system
 *
 * @author SWAD Team
 */
public class GetAllOrdersRR extends AbstractRR {

  /**
   * Creates the REST resource that retrieves all orders.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public GetAllOrdersRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.GET_ORDERS, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final ResourceList<Order> orders = new GetAllOrdersDAO().access().getOutputParam();

      res.setStatus(HttpServletResponse.SC_OK);
      orders.toJSON(res.getOutputStream());

    } catch (final SQLException e) {
      LOGGER.error("Database error (no. %d) while retrieving all orders: %s", e.getErrorCode(),
          e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      new Message("Database error while retrieving orders.", ErrorCodes.UNEXPECTED_DB_ERROR,
          e.getMessage()).toJSON(res.getOutputStream());
    }
  }
}
