/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.Ingredient;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class CreateIngredientDAO extends AbstractDAO<Ingredient> {

  private static final String STATEMENT =
      "INSERT INTO ingredients(name, allergen, is_frozen) VALUES (?, ?, ?) RETURNING *";

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
      // Convert the list of allergens to an SQL array
      final String[] allergenNames =
          ingredient.getAllergens() != null
              ? ingredient.getAllergens().stream().map(Enum::name).toArray(String[]::new)
              : new String[0];
      final Array sqlAllergens = con.createArrayOf("allergen", allergenNames);

      // Set the parameters for the prepared statement
      stmt.setString(1, ingredient.getName());
      stmt.setArray(2, sqlAllergens);
      stmt.setBoolean(3, ingredient.isFrozen());

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          // Create a list to hold the allergens
          List<Allergen> allergens = new ArrayList<>();

          // Convert the SQL array back to a list of allergens
          Array allergenArray = rs.getArray("allergen");
          if (allergenArray != null) {
            for (String a : (String[]) allergenArray.getArray()) {
              allergens.add(Allergen.valueOf(a));
            }
          }

          // Create the ingredient object from the result set
          i =
              new Ingredient(
                  rs.getInt("id"), rs.getString("name"), allergens, rs.getBoolean("is_frozen"));
        }
      }
    }

    // Set the output parameter to the created ingredient
    outputParam = i;
  }
}
