package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidIngredientException;
import com.swad.taptable.resources.Ingredient;

import java.sql.Array;
import java.sql.PreparedStatement;

public class EditIngredientDAO extends AbstractDAO<Ingredient> {

  private final Ingredient editedIngredient;

  private final String STATEMENT =
      "UPDATE ingredients SET name=?, is_frozen=?, allergen=? WHERE id=?";

  /**
   * Creates a new DAO object.
   *
   * @param editedIngredient the ingredient with updated values.
   */
  public EditIngredientDAO(final Ingredient editedIngredient) throws NotValidIngredientException {
    if (editedIngredient == null) {
      throw new NotValidIngredientException("Invalid ingredient");
    }

    this.editedIngredient = editedIngredient;
  }

  @Override
  protected void doAccess() throws Exception {
    PreparedStatement ps = con.prepareStatement(STATEMENT);

    // FIXME: to be fixed
    final String[] allergenNames =
        editedIngredient.getAllergens().stream().map(Enum::name).toArray(String[]::new);
    final Array sqlAllergens = con.createArrayOf("allergen", allergenNames);

    ps.setString(1, editedIngredient.getName());
    ps.setBoolean(2, editedIngredient.isFrozen());
    ps.setArray(3, sqlAllergens);
    ps.setInt(4, editedIngredient.getId());

    ps.execute();
    ps.close();
  }
}
