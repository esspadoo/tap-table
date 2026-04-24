package com.swad.taptable.dao.promotions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Promotion;
import com.swad.taptable.resources.ResourceList;

public class GetPromotionsDAO extends AbstractDAO<ResourceList<Promotion>> {

  private static final String STATEMENT =
      "SELECT code, discount, description, valid_from, valid_to FROM promotions";

  @Override
  protected void doAccess() throws Exception {
    List<Promotion> promotions = new ArrayList<>();
    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);) {
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          promotions.add(new Promotion(rs.getString("code"), rs.getFloat("discount"),
              rs.getString("description"), rs.getTimestamp("valid_from").toLocalDateTime(),
              rs.getTimestamp("valid_to").toLocalDateTime()));
        }
      }
    }

    outputParam = new ResourceList<>(promotions);
  }

}
