package com.swad.taptable.dao.ingredient;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.Ingredient;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetIngredientDAO extends AbstractDAO<Ingredient> {
    private final int ingredientId;

    public GetIngredientDAO(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    @Override
    protected void doAccess() throws Exception {
        final String STATEMENT = "SELECT id, name, allergen, is_frozen " +
                "FROM ingredients WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Leggi l'array di allergeni da PostgreSQL
                List<Allergen> allergens = new ArrayList<>();
                Array allergenArray = rs.getArray("allergen");

                if (allergenArray != null) {
                    String[] allergenStrings = (String[]) allergenArray.getArray();
                    for (String a : allergenStrings) {
                        allergens.add(Allergen.valueOf(a));
                    }
                }

                this.outputParam = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        allergens,
                        rs.getBoolean("is_frozen")
                );
            }
        }catch (SQLException e){
            throw new SQLException("Error accessing ingredient with id " + ingredientId);
        }
    }
}