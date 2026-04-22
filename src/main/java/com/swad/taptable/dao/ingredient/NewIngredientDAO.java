package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidIngredientException;
import com.swad.taptable.resources.Ingredient;

import java.sql.Array;
import java.sql.PreparedStatement;

public class NewIngredientDAO extends AbstractDAO<Ingredient> {

  private final String STATEMENT =
      "INSERT INTO ingredients(name, allergen, is_frozen) VALUES (?, ?, ?)";

  private final Ingredient ingredient;

  /**
   * Creates a new DAO object.
   *
   * @param ingredient the ingredient to be stored into the database.
   */
  protected NewIngredientDAO(final Ingredient ingredient) throws NotValidIngredientException {
    if (ingredient == null) {
      throw new NotValidIngredientException("Invalid ingredient");
    }

    this.ingredient = ingredient;
  }

  @Override
  protected void doAccess() throws Exception {
    PreparedStatement pstmt = con.prepareStatement(STATEMENT);

    try {
      final String[] allergenNames = ingredient.getAllergens().stream()
          .map(Enum::name)
          .toArray(String[]::new);
      final Array sqlAllergens = con.createArrayOf("allergen", allergenNames);

      pstmt.setString(1, ingredient.getName());
      pstmt.setArray(2, sqlAllergens);
      pstmt.setBoolean(3, ingredient.isFrozen());

      pstmt.execute();
    } finally {
      if (pstmt != null) {
        pstmt.close();
      }
    }
  }
}