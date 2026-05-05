/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.dish;

import com.swad.taptable.dao.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteDishDAO extends AbstractDAO<Boolean> {
  private static final String STATEMENT = "DELETE FROM dishes WHERE id = ?";

  private final int dishId;

  public DeleteDishDAO(final int dishId) {
    this.dishId = dishId;
  }

  @Override
  protected void doAccess() throws SQLException {
    boolean isDeleted = false;
    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
      pstmt.setInt(1, dishId);

      /**
       * PreparedStatement::executeUpdate() returns the number of rows affected by the query. If it
       * returns 1, this means that one row was deleted, which it was intended. Otherwise, if it
       * returns 0, it means that no rows were deleted, which could indicate that the dish with the
       * provided ID doesn't exist
       */
      isDeleted = pstmt.executeUpdate() == 1;
    }

    outputParam = isDeleted;
  }
}
