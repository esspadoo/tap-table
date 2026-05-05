/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.promotions;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.PromotionUsageFlag;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Returns {@code true} if the given user has already applied the promotion code on a past order,
 * {@code false} otherwise.
 */
public class CheckPromotionUsageDAO extends AbstractDAO<PromotionUsageFlag> {

  private static final String STATEMENT =
      "SELECT COUNT(*) FROM orders JOIN promotions ON promotions.id = orders.promotion_id WHERE orders.user_id=? AND promotions.code=?";

  private final int userId;
  private final String code;

  /**
   * Creates a new DAO object.
   *
   * @param userId the ID of the user to check.
   * @param code the promotion code to check
   */
  public CheckPromotionUsageDAO(final int userId, final String code) {
    this.userId = userId;
    this.code = code;
  }

  @Override
  protected void doAccess() throws Exception {
    PromotionUsageFlag p = null;
    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setInt(1, userId);
      ps.setString(2, code);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          p = new PromotionUsageFlag(rs.getInt(1) > 0);
        }
      }
    }

    outputParam = p;
  }
}
