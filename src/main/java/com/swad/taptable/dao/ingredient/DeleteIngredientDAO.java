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

/**
 * Data access object for deleting an ingredient from the database by its {@code id}.
 * 
 * @author SWAD Team
 */
public class DeleteIngredientDAO extends AbstractDAO<Ingredient> {
  private static final String STATEMENT = "DELETE FROM ingredients WHERE id = ? RETURNING *";

  private final Integer id;

  /**
   * Constructor for DeleteIngredientDAO.
   * 
   * @param ingredient the {@code id} of the ingredient to be deleted from the database.
   */
  public DeleteIngredientDAO(final Integer id) {
    this.id = id;
  }

  @Override
  protected void doAccess() throws SQLException {
    Ingredient i = null;

    // Execute the prepared statement to delete the ingredient and return the deleted ingredient
    try (PreparedStatement preparedStatement = con.prepareStatement(STATEMENT)) {
      // Set the id parameter for the prepared statement
      preparedStatement.setInt(1, id);

      // Execute the query and get the result set
      try (ResultSet rs = preparedStatement.executeQuery()) {

        /**
         * Extract the ingredient from the result set. If the result set is empty, it means that
         * there is no ingredient with the specified id, and the output parameter will be set to
         * null.
         */
        if (rs.next()) {
          // Create a list to hold the allergens
          List<Allergen> allergens = new ArrayList<>();
          Array allergenArray = rs.getArray("allergen");
          if (allergenArray != null) {
            for (String a : (String[]) allergenArray.getArray()) {
              allergens.add(Allergen.valueOf(a));
            }
          }

          // Create the ingredient object from the result set
          i = new Ingredient(rs.getInt("id"), rs.getString("name"), allergens,
              rs.getBoolean("is_frozen"));
        }
      }
    }

    outputParam = i;
  }
}
