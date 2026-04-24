package com.swad.taptable.rest.order;

import com.swad.taptable.dao.order.EditOrderStatusDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderStatus;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class EditOrderStatusRR extends AbstractRR {
  /**
   * Creates a new REST resource.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public EditOrderStatusRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.EDIT_ORDER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int orderId = Integer.parseInt((String) req.getAttribute("order_id"));
      final OrderStatus orderStatus =
          OrderStatus.valueOf((String) req.getAttribute("order_status"));

      final Order orderEdited =
          new EditOrderStatusDAO(orderId, orderStatus).access().getOutputParam();

      if (orderEdited == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m = new Message("Order not found", ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Order with id %d successfully edited", orderId);
      res.setStatus(HttpServletResponse.SC_OK);
      orderEdited.toJSON(res.getOutputStream());
    } catch (NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message("Invalid order ID: must be a number",
          ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (IllegalArgumentException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m =
          new Message("Invalid order status", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (SQLException ex) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Unexpected database error: no. " + ex.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
