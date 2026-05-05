/* Copyright (c) 2026 University of Padua, Italy - MIT License */
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

public class CreateDishDAO extends AbstractDAO<Dish> {
  private static final String DISHES_INSERT_STATEMENT =
      "INSERT INTO dishes(name, description, category_name, price) VALUES (?, ?, ?, ?) RETURNING *";
  private static final String INGREDIENTS_INSERT_STATEMENT =
      "INSERT INTO dish_ingredients(dish_id, ingredient_id) VALUES (?, ?)";
  private static final String INGREDIENTS_SELECT_STATEMENT =
      "SELECT i.id, i.name, i.allergen, i.is_frozen FROM ingredients i JOIN dish_ingredients di ON i.id = di.ingredient_id WHERE di.dish_id = ?";

  private final Dish dish;

  public CreateDishDAO(final Dish dish) {
    this.dish = dish;
  }

  @Override
  protected void doAccess() throws SQLException {
    Dish d = null;
    con.setAutoCommit(false);

    // Insert into dishes and get generated dish ID
    try (PreparedStatement dishesPstmt = con.prepareStatement(DISHES_INSERT_STATEMENT)) {
      dishesPstmt.setString(1, dish.getName());
      dishesPstmt.setString(2, dish.getDescription());
      dishesPstmt.setString(3, dish.getCategory());
      dishesPstmt.setDouble(4, dish.getPrice());

      try (ResultSet rs = dishesPstmt.executeQuery()) {
        if (!rs.next()) {
          // If no row is returned, the insert failed
          throw new SQLException("Insert dish failed, no row returned.");
        }

        // Get generated dish ID and other details returned
        int dishId = rs.getInt("id");
        String dishName = rs.getString("name");
        String dishDescription = rs.getString("description");
        String dishCategory = rs.getString("category_name");
        double dishPrice = rs.getDouble("price");

        // Insert into dish_ingredients
        try (PreparedStatement ingredientsPstmt =
            con.prepareStatement(INGREDIENTS_INSERT_STATEMENT)) {
          // Client should provide an array of ingredient IDs to associate with the dish
          for (int ingredientId : dish.getIngredientIds()) {
            ingredientsPstmt.setInt(1, dishId);
            ingredientsPstmt.setInt(2, ingredientId);

            // If executeUpdate returns 0, the insert failed
            if (ingredientsPstmt.executeUpdate() == 0) {
              throw new SQLException("Insert dish_ingredient failed, no row affected.");
            }
          }
        }

        // Retrieve the detailed list of the ingredient composing the dish
        List<Ingredient> ingredients = new ArrayList<>();
        try (PreparedStatement ingredientsSelectPstmt =
            con.prepareStatement(INGREDIENTS_SELECT_STATEMENT)) {
          ingredientsSelectPstmt.setInt(1, dishId);
          try (ResultSet ingredientsRs = ingredientsSelectPstmt.executeQuery()) {
            while (ingredientsRs.next()) {
              List<Allergen> allergens = new ArrayList<>();
              Array allergenArray = ingredientsRs.getArray("allergen");
              if (allergenArray != null) {
                for (String a : (String[]) allergenArray.getArray()) {
                  allergens.add(Allergen.valueOf(a));
                }
              }
              ingredients.add(
                  new Ingredient(
                      ingredientsRs.getInt("id"),
                      ingredientsRs.getString("name"),
                      allergens,
                      ingredientsRs.getBoolean("is_frozen")));
            }
          }
        }

        d =
            new Dish.Builder()
                .id(dishId)
                .name(dishName)
                .description(dishDescription)
                .category(dishCategory)
                .price(dishPrice)
                .ingredients(ingredients)
                .build();
      }

      con.commit();

      outputParam = d;
    } catch (SQLException e) {
      con.rollback();
      throw e;
    }
  }
}
