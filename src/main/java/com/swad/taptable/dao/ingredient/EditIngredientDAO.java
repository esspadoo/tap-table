/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.ImageType;
import com.swad.taptable.resources.Ingredient;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EditIngredientDAO extends AbstractDAO<Ingredient> {

  private static final String STATEMENT =
      "UPDATE ingredients"
          + " SET name=?, is_frozen=?, allergen=?,"
          + " image = COALESCE(?, image), image_type = COALESCE(?::image_type, image_type)"
          + " WHERE id=? RETURNING *";

  private final Ingredient ingredient;

  /**
   * Creates a new DAO object.
   *
   * @param editedIngredient the ingredient with updated values.
   */
  public EditIngredientDAO(final Ingredient ingredient) {
    this.ingredient = ingredient;
  }

  @Override
  protected void doAccess() throws Exception {
    Ingredient i = null;

    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
      final String[] allergenNames =
          ingredient.getAllergens() != null
              ? ingredient.getAllergens().stream().map(Enum::name).toArray(String[]::new)
              : new String[0];
      final Array sqlAllergens = con.createArrayOf("allergen", allergenNames);

      pstmt.setString(1, ingredient.getName());
      pstmt.setBoolean(2, ingredient.isFrozen());
      pstmt.setArray(3, sqlAllergens);
      pstmt.setBytes(4, ingredient.getImage());
      pstmt.setString(
          5, ingredient.getImageType() != null ? ingredient.getImageType().getMimeType() : null);
      pstmt.setInt(6, ingredient.getId());

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          final List<Allergen> allergens = new ArrayList<>();
          final Array allergenArray = rs.getArray("allergen");
          if (allergenArray != null) {
            for (final String a : (String[]) allergenArray.getArray()) {
              allergens.add(Allergen.valueOf(a));
            }
          }

          i =
              new Ingredient(
                  rs.getInt("id"),
                  rs.getString("name"),
                  allergens,
                  rs.getBoolean("is_frozen"),
                  rs.getBytes("image"),
                  ImageType.fromMimeType(rs.getString("image_type")));
        }
      }
    }

    outputParam = i;
  }
}
