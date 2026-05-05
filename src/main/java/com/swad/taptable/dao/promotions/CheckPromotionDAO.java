/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.promotions;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Promotion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckPromotionDAO extends AbstractDAO<Promotion> {

  private static final String STATEMENT =
      "SELECT code, discount, description, valid_from, valid_to FROM promotions WHERE code=?";

  private final String code;

  /**
   * Creates a new DAO object.
   *
   * @param code the promotion code to look up.
   */
  public CheckPromotionDAO(final String code) {
    this.code = code;
  }

  @Override
  protected void doAccess() throws SQLException {
    Promotion p = null;

    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
      pstmt.setString(1, code);

      try (ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
          p =
              new Promotion(
                  rs.getString("code"),
                  rs.getFloat("discount"),
                  rs.getString("description"),
                  rs.getTimestamp("valid_from").toLocalDateTime(),
                  rs.getTimestamp("valid_to").toLocalDateTime());
        }
      }
    }

    this.outputParam = p;
  }
}
