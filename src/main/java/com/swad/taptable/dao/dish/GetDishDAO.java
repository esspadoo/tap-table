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

public class GetDishDAO extends AbstractDAO<Dish> {
    private static final String DISH_STATEMENT = "SELECT * FROM dishes WHERE id = ?";
    private static final String INGREDIENTS_STATEMENT =
            "SELECT i.id, i.name, i.allergen, i.is_frozen FROM ingredients i "
                    + "JOIN dish_ingredients di ON i.id = di.ingredient_id WHERE di.dish_id = ?";

    private final int dishId;

    public GetDishDAO(final int dishId) {
        this.dishId = dishId;
    }

    @Override
    protected void doAccess() throws SQLException {
        Dish d = null;

        try (PreparedStatement dishPstmt = con.prepareStatement(DISH_STATEMENT)) {
            dishPstmt.setInt(1, dishId);

            try (ResultSet dishRs = dishPstmt.executeQuery()) {
                if (dishRs.next()) {
                    List<Ingredient> ingredients = new ArrayList<>();
                    try (PreparedStatement ingredientsPstmt =
                            con.prepareStatement(INGREDIENTS_STATEMENT)) {
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
                                ingredients.add(new Ingredient(ingredientsRs.getInt("id"),
                                        ingredientsRs.getString("name"), allergens,
                                        ingredientsRs.getBoolean("is_frozen")));
                            }
                        }
                    }

                    d = new Dish.Builder().id(dishRs.getInt("id")).name(dishRs.getString("name"))
                            .description(dishRs.getString("description"))
                            .category(dishRs.getString("category_name"))
                            .price(dishRs.getDouble("price")).ingredients(ingredients).build();
                }
            }
        }

        outputParam = d;
    }
}