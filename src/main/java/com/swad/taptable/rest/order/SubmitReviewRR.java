/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.rest.order;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.dao.order.GetOrderDAO;
import com.swad.taptable.dao.order.SubmitReviewDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
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

public class SubmitReviewRR extends AbstractRR {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();

  public SubmitReviewRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.EDIT_ORDER, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final int userId = Integer.parseInt((String) req.getAttribute("user_id"));
      final int orderId = Integer.parseInt((String) req.getAttribute("order_id"));

      Integer dishId = null;
      Boolean isLiked = null;
      try (JsonParser jp = JSON_FACTORY.createParser(req.getInputStream())) {
        while (jp.nextToken() != JsonToken.END_OBJECT) {
          if (jp.getCurrentToken() != JsonToken.FIELD_NAME) continue;
          switch (jp.currentName()) {
            case "dish_id":
              jp.nextToken();
              dishId = jp.getIntValue();
              break;
            case "is_liked":
              jp.nextToken();
              isLiked = jp.getBooleanValue();
              break;
            default:
              throw new UnexpectedKeyException("Unexpected key: " + jp.currentName());
          }
        }
      }

      if (dishId == null || isLiked == null) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Message m =
            new Message(
                "Missing required fields: dish_id and is_liked.",
                ErrorCodes.INVALID_INPUT_PARAMETER,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final Order order = new GetOrderDAO(orderId).access().getOutputParam();

      if (order == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        final Message m =
            new Message(
                String.format("Order %d not found.", orderId), ErrorCodes.RESOURCE_NOT_FOUND, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      if (!order.getUserId().equals(userId)) {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        final Message m =
            new Message("You do not own this order.", ErrorCodes.FORBIDDEN_OPERATION, null);
        m.toJSON(res.getOutputStream());
        return;
      }

      if (order.getStatus() != OrderStatus.COMPLETED) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        final Message m =
            new Message(
                "Reviews can only be submitted on completed orders.",
                ErrorCodes.INVALID_INPUT_PARAMETER,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      final boolean saved = new SubmitReviewDAO(orderId, dishId, isLiked).access().getOutputParam();

      if (!saved) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        final Message m =
            new Message(
                String.format("Dish %d not found in order %d.", dishId, orderId),
                ErrorCodes.RESOURCE_NOT_FOUND,
                null);
        m.toJSON(res.getOutputStream());
        return;
      }

      LOGGER.info("Review submitted for order %d, dish %d by user %d.", orderId, dishId, userId);
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);

    } catch (final UnexpectedKeyException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message(
              "Malformed JSON in request body.",
              ErrorCodes.WRONG_RESOURCE_PROVIDED,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final NumberFormatException e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final Message m =
          new Message("Invalid ID format.", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
      m.toJSON(res.getOutputStream());
    } catch (final SQLException e) {
      LOGGER.error("Database error while submitting review: %s", e.getMessage());
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      final Message m =
          new Message(
              "Database error while submitting review.",
              ErrorCodes.UNEXPECTED_DB_ERROR,
              e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
