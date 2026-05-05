/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.ResourceList;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for retrieving all the ingredients from the database.
 *
 * @author SWAD Team
 */
public class GetIngredientsDAO extends AbstractDAO<ResourceList<Ingredient>> {

  private static final String STATEMENT =
      "SELECT id, name, allergen, is_frozen FROM ingredients ORDER BY id ASC";

  public GetIngredientsDAO() {}

  @Override
  protected void doAccess() throws Exception {
    List<Ingredient> ingredients = new ArrayList<>();

    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          List<Allergen> allergens = new ArrayList<>();
          Array allergenArray = rs.getArray("allergen");

          if (allergenArray != null) {
            String[] allergenStrings = (String[]) allergenArray.getArray();
            for (String a : allergenStrings) {
              allergens.add(Allergen.valueOf(a));
            }
          }

          ingredients.add(
              new Ingredient(
                  rs.getInt("id"), rs.getString("name"), allergens, rs.getBoolean("is_frozen")));
        }
      }
    }

    this.outputParam = new ResourceList<Ingredient>(ingredients);
  }
}
