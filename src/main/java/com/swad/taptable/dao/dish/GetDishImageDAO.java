/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.dish;

import com.swad.taptable.dao.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class GetDishImageDAO extends AbstractDAO<byte[]> {

  private static final String STATEMENT = "SELECT image FROM dishes WHERE id = ?";

  private final int dishId;

  /**
   * Creates a new DAO object.
   *
   * @param dishId the identifier of the dish whose image is requested.
   */
  public GetDishImageDAO(final int dishId) {
    this.dishId = dishId;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      stmt.setInt(1, dishId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          outputParam = rs.getBytes("image");
        }
      }
    }
  }
}
