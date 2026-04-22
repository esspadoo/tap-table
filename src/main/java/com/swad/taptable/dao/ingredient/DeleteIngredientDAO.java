package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Ingredient;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteIngredientDAO extends AbstractDAO<Boolean> {
    private final Ingredient ingredient;

    public DeleteIngredientDAO(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    protected void doAccess() throws Exception {
        final String STATEMENT = "DELETE FROM ingredients " +
                                 "WHERE id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(STATEMENT)) {
            preparedStatement.setInt(1, ingredient.getId());
            preparedStatement.executeUpdate();
            outputParam = true;
        }catch (SQLException e){
            outputParam = false;
            throw new Exception("Error while deleting ingredient");
        }
    }
}
