package com.swad.taptable.rest.order;

import com.swad.taptable.dao.order.DeleteOrderDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteOrderRR extends AbstractRR {
  /**
   * Creates a new REST resource.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public DeleteOrderRR(HttpServletRequest req, HttpServletResponse res) {
    super(Actions.DELETE_ORDER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      // order_id is passed as a path parameter
      final int orderId = Integer.parseInt((String) req.getAttribute("order_id"));

      final DeleteOrderDAO dao = new DeleteOrderDAO(orderId);
      boolean isDeleted = dao.access().getOutputParam();
      if (isDeleted) {
        LOGGER.info("Order with id %d successfully deleted.", orderId);
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
      } else {
        // If it has not been deleted, it means that the order was not found, so we can
        // return a 404 Not Found
        LOGGER.warn("Order with id %d not found for deletion.", orderId);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Message m = new Message("Order with id " + orderId + " not found for deletion",
            ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
      }
    } catch (NumberFormatException e) {
      LOGGER.warn("Invalid format of order ID: " + req.getAttribute("order_id") + " to be deleted");
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      Message m = new Message(
          "Invalid format of order ID " + req.getAttribute("order_id") + " to be deleted",
          ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (SQLException ex) {
      // FIXME: better sql handling (dont return 500 everytime)
      Message m = new Message("Unexpected database error: no. " + ex.getErrorCode(),
          ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      m.toJSON(res.getOutputStream());
    }
  }
}
