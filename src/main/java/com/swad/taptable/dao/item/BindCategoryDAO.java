package com.swad.taptable.dao.item;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidCategoryException;
import com.swad.taptable.resources.Item;
import com.swad.taptable.resources.ItemCategory;

import java.sql.PreparedStatement;

public class BindCategoryDAO extends AbstractDAO<Item> {

  private final Item item;

  /**
   * Creates a new DAO object.
   *
   * @param item the item whose categories are to be bound.
   */
  protected BindCategoryDAO(final Item item) throws NotValidCategoryException {
    for (final ItemCategory itemCategory : item.getCategories()) {
      if (itemCategory.getName() == null || itemCategory.getName().isEmpty()) {
        throw new NotValidCategoryException("Invalid name");
      }

      if (itemCategory.getDescription() == null || itemCategory.getDescription().isEmpty()) {
        throw new NotValidCategoryException("Invalid description");
      }
    }

    this.item = item;
  }

  @Override
  protected void doAccess() throws Exception {
    final String STATEMENT = "INSERT INTO item_categories (item_id, category_id) VALUES (?, ?)";

    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      for (final ItemCategory category : item.getCategories()) {
        ps.setInt(1, item.getId());
        ps.setString(2, category.getName());
      }

      ps.execute();
    }
  }
}
