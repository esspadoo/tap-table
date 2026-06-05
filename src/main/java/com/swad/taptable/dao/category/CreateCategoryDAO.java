/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.category;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Category;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO that inserts a new dish category into the database.
 *
 * @author SWAD Team
 */
public final class CreateCategoryDAO extends AbstractDAO<Category> {

  private static final String STATEMENT = "INSERT INTO categories(name) VALUES (?) RETURNING *";

  private final String name;

  /**
   * Creates a new DAO object.
   *
   * @param name the name of the category to create.
   */
  public CreateCategoryDAO(final String name) {
    this.name = name;
  }

  @Override
  protected void doAccess() throws Exception {
    Category c = null;

    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      stmt.setString(1, name);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          c = new Category(rs.getString("name"));
        }
      }
    }

    outputParam = c;
  }
}
