/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.dish;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Image;
import com.swad.taptable.resources.ImageType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class GetDishImageDAO extends AbstractDAO<Image> {

  private static final String STATEMENT = "SELECT image, image_type FROM dishes WHERE id = ?";

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
          final byte[] bytes = rs.getBytes("image");
          final ImageType type = ImageType.fromMimeType(rs.getString("image_type"));
          if (bytes != null) outputParam = new Image(bytes, type);
        }
      }
    }
  }
}
