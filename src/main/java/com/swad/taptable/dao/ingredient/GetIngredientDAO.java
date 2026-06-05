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

/**
 * DAO that retrieves a single ingredient by its identifier.
 *
 * @author SWAD Team
 */
public class GetIngredientDAO extends AbstractDAO<Ingredient> {
  // We know that we can avoid to get the id from the select, but it isn't a big deal
  private static final String STATEMENT =
      "SELECT id, name, allergen, is_frozen FROM ingredients WHERE id = ?";

  private final int ingredientId;

  /**
   * Creates a new DAO object.
   *
   * @param ingredientId the identifier of the ingredient to retrieve.
   */
  public GetIngredientDAO(final int ingredientId) {
    this.ingredientId = ingredientId;
  }

  @Override
  protected void doAccess() throws Exception {
    Ingredient i = null;

    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setInt(1, ingredientId);

      try (ResultSet rs = ps.executeQuery()) {
        // We expect only one result since id is unique, so we can use if instead of while.
        if (rs.next()) {
          // Create a list to hold the allergens
          List<Allergen> allergens = new ArrayList<>();
          Array allergenArray = rs.getArray("allergen");

          if (allergenArray != null) {
            for (String a : (String[]) allergenArray.getArray()) {
              allergens.add(Allergen.valueOf(a));
            }
          }

          // Create the ingredient object with the retrieved values
          i =
              new Ingredient(
                  rs.getInt("id"), rs.getString("name"), allergens, rs.getBoolean("is_frozen"));
        }
      }
    }

    outputParam = i;
  }
}
