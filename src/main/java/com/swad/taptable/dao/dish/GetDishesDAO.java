/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.dish;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.ResourceList;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO that retrieves all dishes together with their ingredient lists from the database.
 *
 * @author SWAD Team
 */
public class GetDishesDAO extends AbstractDAO<ResourceList<Dish>> {
  private static final String DISHES_STATEMENT = "SELECT * FROM dishes";
  private static final String INGREDIENTS_STATEMENT =
      "SELECT i.id, i.name, i.allergen, i.is_frozen FROM ingredients i JOIN dish_ingredients di ON i.id = di.ingredient_id WHERE di.dish_id = ?";

  @Override
  protected void doAccess() throws SQLException {
    List<Dish> dishes = new ArrayList<>();

    try (PreparedStatement dishesPstmt = con.prepareStatement(DISHES_STATEMENT);
        ResultSet dishesRs = dishesPstmt.executeQuery()) {

      while (dishesRs.next()) {
        int dishId = dishesRs.getInt("id");

        List<Ingredient> ingredients = new ArrayList<>();
        try (PreparedStatement ingredientsPstmt = con.prepareStatement(INGREDIENTS_STATEMENT)) {
          ingredientsPstmt.setInt(1, dishId);
          try (ResultSet ingredientsRs = ingredientsPstmt.executeQuery()) {
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

        dishes.add(
            new Dish.Builder()
                .id(dishId)
                .name(dishesRs.getString("name"))
                .description(dishesRs.getString("description"))
                .price(dishesRs.getDouble("price"))
                .ingredients(ingredients)
                .category(dishesRs.getString("category_name"))
                .build());
      }
    }

    outputParam = new ResourceList<>(dishes);
  }
}
