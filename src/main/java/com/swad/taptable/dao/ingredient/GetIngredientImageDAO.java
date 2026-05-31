/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO for retrieving the image of an ingredient from the database.
 * 
 * @author SWAD Team
 */
public final class GetIngredientImageDAO extends AbstractDAO<byte[]> {

  private static final String STATEMENT = "SELECT image FROM ingredients WHERE id = ?";

  private final int ingredientId;

  /**
   * Creates a new DAO object.
   *
   * @param ingredientId the identifier of the ingredient whose image is requested.
   */
  public GetIngredientImageDAO(final int ingredientId) {
    this.ingredientId = ingredientId;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      stmt.setInt(1, ingredientId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          outputParam = rs.getBytes("image");
        }
      }
    }
  }
}
