/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO that records a user's like or dislike review for a dish in an existing order.
 *
 * @author SWAD Team
 */
public final class SubmitReviewDAO extends AbstractDAO<Boolean> {

  private static final String STATEMENT =
      "UPDATE order_dishes SET is_liked = ? WHERE order_id = ? AND dish_id = ? RETURNING dish_id";

  private final int orderId;
  private final int dishId;
  private final boolean isLiked;

  /**
   * Creates a new DAO object.
   *
   * @param orderId the identifier of the order containing the reviewed dish.
   * @param dishId the identifier of the dish being reviewed.
   * @param isLiked {@code true} if the user liked the dish, {@code false} otherwise.
   */
  public SubmitReviewDAO(final int orderId, final int dishId, final boolean isLiked) {
    this.orderId = orderId;
    this.dishId = dishId;
    this.isLiked = isLiked;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setBoolean(1, isLiked);
      ps.setInt(2, orderId);
      ps.setInt(3, dishId);
      try (ResultSet rs = ps.executeQuery()) {
        outputParam = rs.next();
      }
    }
  }
}
