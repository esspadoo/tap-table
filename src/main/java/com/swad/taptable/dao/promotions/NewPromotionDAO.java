package com.swad.taptable.dao.promotions;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Promotion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class NewPromotionDAO extends AbstractDAO<Promotion> {

  private static final String STATEMENT =
      "INSERT INTO promotions(code, type, description, valid_from, valid_to) VALUES (?, ?, ?, ?, ?) RETURNING *";

  private final Promotion promotion;

  /**
   * Creates a new DAO object.
   *
   * @param promotion the promotion to be stored into the database.
   */
  public NewPromotionDAO(final Promotion promotion) {
    this.promotion = promotion;
  }

  @Override
  protected void doAccess() throws SQLException {
    Promotion p = null;

    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);) {

      pstmt.setString(1, promotion.getCode());
      pstmt.setString(2, promotion.getType());
      pstmt.setString(3, promotion.getDescription());
      pstmt.setTimestamp(4, Timestamp.valueOf(promotion.getValidFrom()));
      pstmt.setTimestamp(5, Timestamp.valueOf(promotion.getValidTo()));

      try (ResultSet rs = pstmt.executeQuery();) {
        if (!rs.next()) {
          throw new SQLException("Error while inserting the promotion");
        }
        p = new Promotion(rs.getString("code"), rs.getString("type"), rs.getString("description"),
            rs.getTimestamp("valid_from").toLocalDateTime(),
            rs.getTimestamp("valid_to").toLocalDateTime());
      }
    }

    outputParam = p;
  }
}
