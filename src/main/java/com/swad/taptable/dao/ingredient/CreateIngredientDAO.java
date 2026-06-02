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

public final class CreateIngredientDAO extends AbstractDAO<Ingredient> {

  private static final String STATEMENT =
      "INSERT INTO ingredients(name, allergen, is_frozen, image, image_type)"
          + " VALUES (?, ?, ?, ?, ?::image_type) RETURNING *";

  private final Ingredient ingredient;

  /**
   * Creates a new DAO object.
   *
   * @param ingredient the ingredient to be stored into the database.
   */
  public CreateIngredientDAO(final Ingredient ingredient) {
    this.ingredient = ingredient;
  }

  @Override
  protected void doAccess() throws Exception {
    Ingredient i = null;

    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      final String[] allergenNames =
          ingredient.getAllergens() != null
              ? ingredient.getAllergens().stream().map(Enum::name).toArray(String[]::new)
              : new String[0];
      final Array sqlAllergens = con.createArrayOf("allergen", allergenNames);

      stmt.setString(1, ingredient.getName());
      stmt.setArray(2, sqlAllergens);
      stmt.setBoolean(3, ingredient.isFrozen() != null && ingredient.isFrozen());
      stmt.setBytes(4, ingredient.getImage());
      stmt.setString(
          5, ingredient.getImageType() != null ? ingredient.getImageType().getMimeType() : null);

      try (ResultSet rs = stmt.executeQuery()) {
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
