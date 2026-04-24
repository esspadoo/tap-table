package com.swad.taptable.dao.category;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class DeleteCategoryDAO extends AbstractDAO<Category> {

    private static final String STATEMENT =
        "DELETE FROM categories WHERE name = ? RETURNING *";

    private final String name;

    public DeleteCategoryDAO(final String name) {
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
