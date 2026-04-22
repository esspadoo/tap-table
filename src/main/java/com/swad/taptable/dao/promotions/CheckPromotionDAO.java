package com.swad.taptable.dao.promotions;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidPromotionException;
import com.swad.taptable.resources.Promotion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckPromotionDAO extends AbstractDAO<Promotion> {

  private static final String STATEMENT =
      "SELECT code, type, description, valid_from, valid_to " + "FROM promotions WHERE code=?";

  private final String code;

  /**
   * Creates a new DAO object.
   *
   * @param code the promotion code to look up.
   */
  protected CheckPromotionDAO(final String code) throws NotValidPromotionException {
    if (code == null) {
      throw new NotValidPromotionException("Invalid promotion code");
    }

    this.code = code;
  }

  @Override
  protected void doAccess() throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    final List<Promotion> promotionList = new ArrayList<>();

    try {
      pstmt = con.prepareStatement(STATEMENT);

      pstmt.setString(1, code);

      rs = pstmt.executeQuery();

      while (rs.next()) {
        promotionList.add(new Promotion(rs.getString(1), rs.getString(2), rs.getString(3),
            rs.getObject(4, LocalDateTime.class), rs.getObject(5, LocalDateTime.class)));
      }

    } finally {
      if (rs != null) {
        rs.close();
      }

      if (pstmt != null) {
        pstmt.close();
      }
    }

    if (promotionList.isEmpty()) {
      throw new NotValidPromotionException("Invalid promotion code");
    }

    final Promotion promotion = promotionList.getFirst();

    if (promotion.getValidTo().isAfter(LocalDateTime.now())
        || promotion.getValidFrom().isAfter(LocalDateTime.now())) {
      throw new NotValidPromotionException("Promotion code expired");
    }

    this.outputParam = promotion;
  }
}
