package com.swad.taptable.dao.dish;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.Ingredient;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditDishDAO extends AbstractDAO<Dish> {
    private static final String UPDATE_DISHES_STATEMENT =
            "UPDATE dishes SET name = ?, description = ?, category_name = ?, price = ? WHERE id = ? RETURNING *";
    private static final String DELETE_INGREDIENTS_STATEMENT =
            "DELETE FROM dish_ingredients WHERE dish_id = ?";
    private static final String INSERT_INGREDIENTS_STATEMENT =
            "INSERT INTO dish_ingredients(dish_id, ingredient_id) VALUES (?, ?)";
    private static final String SELECT_INGREDIENTS_STATEMENT =
            "SELECT i.id, i.name, i.allergen, i.is_frozen FROM ingredients i JOIN dish_ingredients di ON i.id = di.ingredient_id WHERE di.dish_id = ?";

    private final Dish dish;

    public EditDishDAO(final Dish dish) {
        this.dish = dish;
    }

    @Override
    protected void doAccess() throws SQLException {
        Dish d = null;
        con.setAutoCommit(false);

        // Update the dish's related informations (no bindings)
        try (PreparedStatement dishesPstmt = con.prepareStatement(UPDATE_DISHES_STATEMENT)) {
            dishesPstmt.setString(1, dish.getName());
            dishesPstmt.setString(2, dish.getDescription());
            dishesPstmt.setString(3, dish.getCategory());
            dishesPstmt.setDouble(4, dish.getPrice());
            dishesPstmt.setInt(5, dish.getId());

            try (ResultSet rs = dishesPstmt.executeQuery()) {
                if (!rs.next()) {
                    // early return if the dish with the provided ID doesn't exist
                    return;
                }

                // Retrieve the updated dish's information from the ResultSet
                int dishId = rs.getInt("id");
                String dishName = rs.getString("name");
                String dishDescription = rs.getString("description");
                String dishCategory = rs.getString("category_name");
                double dishPrice = rs.getDouble("price");

                // Delete the existing ingredients associations
                try (PreparedStatement deleteIngredientsPstmt =
                        con.prepareStatement(DELETE_INGREDIENTS_STATEMENT)) {
                    deleteIngredientsPstmt.setInt(1, dishId);
                    deleteIngredientsPstmt.executeUpdate();
                }

                // Insert the new ingredients associations
                try (PreparedStatement insertIngredientPstmt =
                        con.prepareStatement(INSERT_INGREDIENTS_STATEMENT)) {
                    for (int ingredientId : dish.getIngredientIds()) {
                        insertIngredientPstmt.setInt(1, dishId);
                        insertIngredientPstmt.setInt(2, ingredientId);

                        if (insertIngredientPstmt.executeUpdate() == 0) {
                            throw new SQLException(
                                    "Insert dish_ingredient failed, no row affected.");
                        }
                    }
                }

                // Retrieve the updated ingredients of the dish
                List<Ingredient> ingredients = new ArrayList<>();
                try (PreparedStatement selectIngredientsPstmt =
                        con.prepareStatement(SELECT_INGREDIENTS_STATEMENT)) {
                    selectIngredientsPstmt.setInt(1, dishId);
                    try (ResultSet ingredientsRs = selectIngredientsPstmt.executeQuery()) {
                        while (ingredientsRs.next()) {
                            List<Allergen> allergens = new ArrayList<>();
                            Array allergenArray = ingredientsRs.getArray("allergen");
                            if (allergenArray != null) {
                                for (String a : (String[]) allergenArray.getArray()) {
                                    allergens.add(Allergen.valueOf(a));
                                }
                            }
                            ingredients.add(new Ingredient(ingredientsRs.getInt("id"),
                                    ingredientsRs.getString("name"), allergens,
                                    ingredientsRs.getBoolean("is_frozen")));
                        }
                    }
                }

                d = new Dish.Builder().id(dishId).name(dishName).description(dishDescription)
                        .category(dishCategory).price(dishPrice).ingredients(ingredients).build();
            }

            con.commit();
            outputParam = d;
        } catch (SQLException e) {
            // Rollback the transaction in case of any failure during the update process
            con.rollback();
            throw e;
        }
    }
}