package com.swad.taptable.dao.item;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidIngredientException;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Item;

import java.sql.PreparedStatement;

public class BindIngredientDAO extends AbstractDAO<Item> {

  private final Item item;

  /**
   * Creates a new DAO object.
   *
   * @param item the item whose ingredients are to be bound.
   */
  public BindIngredientDAO(final Item item) {
    this.item = item;
  }

  @Override
  protected void doAccess() throws Exception {
    final String STATEMENT = "INSERT INTO item_ingredients(item_id, ingredient_id) VALUES (?, ?)";

    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
      for (final Ingredient ingredient : item.getIngredients()) {
        if (ingredient == null) {
          throw new NotValidIngredientException("Invalid null ingredient");
        }

        pstmt.setInt(1, item.getId());
        pstmt.setInt(2, ingredient.getId());
      }

      pstmt.execute();
    }
  }
}
