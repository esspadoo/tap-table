package com.swad.taptable.dao.promotions;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidPromotionException;
import com.swad.taptable.resources.Promotion;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class NewPromotionDAO extends AbstractDAO<Promotion> {

  private static final String STATEMENT = "INSERT INTO promotions VALUES (?, ?, ?, ?, ?)";

  private final Promotion promotion;

  /**
   * Creates a new DAO object.
   *
   * @param promotion the promotion to be stored into the database.
   */
  protected NewPromotionDAO(final Promotion promotion) throws NotValidPromotionException {
    if (promotion == null) {
      throw new NotValidPromotionException("Invalid promotion");
    }

    if (promotion.getValidTo().isAfter(LocalDateTime.now())
        || promotion.getValidFrom().isAfter(LocalDateTime.now())) {
      throw new NotValidPromotionException("Promotion code expired");
    }

    this.promotion = promotion;
  }

  @Override
  protected void doAccess() throws Exception {
    PreparedStatement pstmt = null;

    try {
      pstmt = con.prepareStatement(STATEMENT);

      pstmt.setString(1, promotion.getCode());
      pstmt.setString(2, promotion.getType());
      pstmt.setString(3, promotion.getDescription());
      pstmt.setObject(4, promotion.getValidFrom());
      pstmt.setObject(5, promotion.getValidTo());

      pstmt.execute();
    } finally {
      if (pstmt != null) {
        pstmt.close();
      }
    }
  }
}