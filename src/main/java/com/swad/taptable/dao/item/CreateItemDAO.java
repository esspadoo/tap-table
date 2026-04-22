package com.swad.taptable.dao.item;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidIngredientException;
import com.swad.taptable.resources.Item;

import java.sql.PreparedStatement;

public class CreateItemDAO extends AbstractDAO<Item> {

  private final Item item;

  /**
   * Creates a new DAO object.
   *
   * @param item the item to be stored into the database.
   */
  protected CreateItemDAO(final Item item) throws NotValidIngredientException {
    if (item == null) {
      throw new NotValidIngredientException("Item cannot be null");
    }

    if (item.getIngredients() == null) {
      throw new NotValidIngredientException("Item cannot have null ingredients");
    }

    if (item.getName() == null || item.getName().isEmpty()) {
      throw new NotValidIngredientException("Item cannot have null name");
    }

    if (item.getBasePrice() < 0) {
      throw new NotValidIngredientException("Item cannot have negative price");
    }

    if (item.getDescription() == null || item.getDescription().isEmpty()) {
      throw new NotValidIngredientException("Item cannot have null description");
    }

    this.item = item;
  }

  @Override
  protected void doAccess() throws Exception {
    final String STATEMENT = "INSERT INTO items (name, description, price) VALUES (?, ?, ?)";

    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
      pstmt.setString(1, item.getName());
      pstmt.setString(2, item.getDescription());
      pstmt.setInt(3, item.getBasePrice());
      pstmt.execute();
    }
  }
}
