package com.swad.taptable.dao.item;

import com.swad.taptable.dao.AbstractDAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteItemDAO extends AbstractDAO<Boolean> {
    private final int itemId;

    public DeleteItemDAO(int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected void doAccess() throws Exception {
        final String STATEMENT = "DELETE FROM items WHERE id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(STATEMENT)) {
            preparedStatement.setInt(1, itemId);
            preparedStatement.executeUpdate();
            outputParam = true;
        }catch (SQLException e){
            outputParam = false;
            throw new SQLException("Error accessing ingredient with id " + itemId);
        }
    }
}
