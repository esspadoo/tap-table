package com.swad.taptable.dao.category;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Category;
import com.swad.taptable.resources.ResourceList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class GetAllCategoriesDAO extends AbstractDAO<ResourceList<Category>> {

  private static final String STATEMENT = "SELECT name FROM categories ORDER BY name ASC";

  @Override
  protected void doAccess() throws Exception {
    List<Category> categories = new ArrayList<>();

    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          categories.add(new Category(rs.getString("name")));
        }
      }
    }

    outputParam = new ResourceList<>(categories);
  }
}
