/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.dish;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Allergen;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.ImageType;
import com.swad.taptable.resources.Ingredient;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateDishDAO extends AbstractDAO<Dish> {
  private static final String DISHES_INSERT_STATEMENT =
      "INSERT INTO dishes(name, description, category_name, price, image, image_type)"
          + " VALUES (?, ?, ?, ?, ?, ?::image_type) RETURNING *";
  private static final String INGREDIENTS_INSERT_STATEMENT =
      "INSERT INTO dish_ingredients(dish_id, ingredient_id) VALUES (?, ?)";
  private static final String INGREDIENTS_SELECT_STATEMENT =
      "SELECT i.id, i.name, i.allergen, i.is_frozen"
          + " FROM ingredients i JOIN dish_ingredients di ON i.id = di.ingredient_id"
          + " WHERE di.dish_id = ?";

  private final Dish dish;

  public CreateDishDAO(final Dish dish) {
    this.dish = dish;
  }

  @Override
  protected void doAccess() throws SQLException {
    Dish d = null;
    con.setAutoCommit(false);

    try (PreparedStatement dishesPstmt = con.prepareStatement(DISHES_INSERT_STATEMENT)) {
      dishesPstmt.setString(1, dish.getName());
      dishesPstmt.setString(2, dish.getDescription());
      dishesPstmt.setString(3, dish.getCategory());
      dishesPstmt.setDouble(4, dish.getPrice());
      dishesPstmt.setBytes(5, dish.getImage());
      dishesPstmt.setString(
          6, dish.getImageType() != null ? dish.getImageType().getMimeType() : null);

      try (ResultSet rs = dishesPstmt.executeQuery()) {
        if (!rs.next()) {
          throw new SQLException("Insert dish failed, no row returned.");
        }

        final int dishId = rs.getInt("id");
        final String dishName = rs.getString("name");
        final String dishDescription = rs.getString("description");
        final String dishCategory = rs.getString("category_name");
        final double dishPrice = rs.getDouble("price");
        final byte[] dishImage = rs.getBytes("image");
        final ImageType dishImageType = ImageType.fromMimeType(rs.getString("image_type"));

        try (PreparedStatement ingredientsPstmt =
            con.prepareStatement(INGREDIENTS_INSERT_STATEMENT)) {
          for (final int ingredientId : dish.getIngredientIds()) {
            ingredientsPstmt.setInt(1, dishId);
            ingredientsPstmt.setInt(2, ingredientId);
            if (ingredientsPstmt.executeUpdate() == 0) {
              throw new SQLException("Insert dish_ingredient failed, no row affected.");
            }
          }
        }

        final List<Ingredient> ingredients = new ArrayList<>();
        try (PreparedStatement ingredientsSelectPstmt =
            con.prepareStatement(INGREDIENTS_SELECT_STATEMENT)) {
          ingredientsSelectPstmt.setInt(1, dishId);
          try (ResultSet ingredientsRs = ingredientsSelectPstmt.executeQuery()) {
            while (ingredientsRs.next()) {
              final List<Allergen> allergens = new ArrayList<>();
              final Array allergenArray = ingredientsRs.getArray("allergen");
              if (allergenArray != null) {
                for (final String a : (String[]) allergenArray.getArray()) {
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
                .image(dishImage)
                .imageType(dishImageType)
                .build();
      }

      con.commit();
      outputParam = d;
    } catch (final SQLException e) {
      con.rollback();
      throw e;
    }
  }
}
