/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.NotValidDishException;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON representation of a menu dish.
 *
 * @author SWAD Team
 */
public class Dish extends AbstractResource {
  private final Integer id;
  private final String name;
  private final String description;
  private final Double price;
  private final List<Integer> ingredientIds;
  private final List<Ingredient> ingredients;
  private final String category;

  /**
   * Creates a new {@code Dish} from the values collected by the builder.
   *
   * @param builder the builder containing the dish data.
   */
  private Dish(final Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.price = builder.price;
    this.ingredientIds = builder.ingredientIds;
    this.ingredients = builder.ingredients;
    this.category = builder.category;
  }

  /**
   * Returns the dish identifier.
   *
   * @return the dish identifier.
   */
  public Integer getId() {
    return id;
  }

  /**
   * Returns the dish name.
   *
   * @return the dish name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the dish description.
   *
   * @return the dish description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the dish price.
   *
   * @return the dish price.
   */
  public Double getPrice() {
    return price;
  }

  /**
   * Returns the identifiers of the ingredients associated with the dish.
   *
   * @return the ingredient identifiers.
   */
  public List<Integer> getIngredientIds() {
    return ingredientIds;
  }

  /**
   * Returns the fully populated ingredient resources associated with the dish.
   *
   * @return the ingredient resources.
   */
  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  /**
   * Returns the category name assigned to the dish.
   *
   * @return the category name.
   */
  public String getCategory() {
    return category;
  }

  /**
   * Parses a {@code Dish} from a JSON payload.
   *
   * @param in the input stream containing the JSON payload.
   * @return the parsed dish.
   * @throws IOException if an error occurs while reading the stream.
   * @throws NotValidDishException if the status of the parsed payload is considered invalid by the
   *     caller contract.
   * @throws UnexpectedKeyException if the payload contains unsupported fields.
   */
  public static Dish fromJSON(final InputStream in)
      throws IOException, NotValidDishException, UnexpectedKeyException {
    Integer jId = null;
    String jName = null;
    String jDescription = null;
    Double jPrice = null;
    List<Integer> jIngredientIds = new ArrayList<>();
    String jCategory = null;

    final JsonParser jp = JSON_FACTORY.createParser(in);

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
        continue;
      }

      switch (jp.currentName()) {
        case "id":
          jp.nextToken();
          jId = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue();
          break;
        case "name":
          jp.nextToken();
          jName = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
          break;
        case "description":
          jp.nextToken();
          jDescription = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
          break;
        case "price":
          jp.nextToken();
          jPrice = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getDoubleValue();
          break;
        case "ingredient_ids":
          if (jp.nextToken() == JsonToken.VALUE_NULL) {
            jIngredientIds = null;
            break;
          }
          while (jp.nextToken() != JsonToken.END_ARRAY) {
            jIngredientIds.add(
                jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue());
          }
          break;
        case "category":
          jp.nextToken();
          jCategory = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
          break;
        default:
          throw new UnexpectedKeyException("Unexpected field: " + jp.currentName());
      }
    }

    return new Dish.Builder()
        .id(jId)
        .name(jName)
        .description(jDescription)
        .price(jPrice)
        .ingredientIds(jIngredientIds)
        .category(jCategory)
        .build();
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    if (id == null) jg.writeNullField("id");
    else jg.writeNumberField("id", id);

    if (name == null) jg.writeNullField("name");
    else jg.writeStringField("name", name);

    if (description == null) jg.writeNullField("description");
    else jg.writeStringField("description", description);

    if (price == null) jg.writeNullField("price");
    else jg.writeNumberField("price", price);

    if (ingredients == null) {
      jg.writeNullField("ingredients");
    } else {
      jg.writeArrayFieldStart("ingredients");
      for (final Ingredient ingredient : ingredients) {
        jg.writeStartObject();

        if (ingredient.getId() == null) jg.writeNullField("id");
        else jg.writeNumberField("id", ingredient.getId());

        if (ingredient.getName() == null) jg.writeNullField("name");
        else jg.writeStringField("name", ingredient.getName());

        if (ingredient.getAllergens() == null) {
          jg.writeNullField("allergens");
        } else {
          jg.writeArrayFieldStart("allergens");
          for (final Allergen a : ingredient.getAllergens()) {
            jg.writeString(a.name());
          }
          jg.writeEndArray();
        }

        if (ingredient.isFrozen() == null) jg.writeNullField("is_frozen");
        else jg.writeBooleanField("is_frozen", ingredient.isFrozen());

        jg.writeEndObject();
      }
      jg.writeEndArray();
    }

    if (category == null) jg.writeNullField("category");
    else jg.writeStringField("category", category);

    jg.writeEndObject();

    jg.flush();
  }

  /** Builder used to assemble {@link Dish} instances. */
  public static class Builder {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private List<Integer> ingredientIds;
    private List<Ingredient> ingredients;
    private String category;

    /**
     * Sets the dish identifier.
     *
     * @param id the dish identifier.
     * @return this builder.
     */
    public Builder id(final Integer id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the dish name.
     *
     * @param name the dish name.
     * @return this builder.
     */
    public Builder name(final String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the dish description.
     *
     * @param description the dish description.
     * @return this builder.
     */
    public Builder description(final String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the dish price.
     *
     * @param price the dish price.
     * @return this builder.
     */
    public Builder price(final Double price) {
      this.price = price;
      return this;
    }

    /**
     * Sets the ingredient identifiers used by the dish.
     *
     * @param ingredientIds the ingredient identifiers.
     * @return this builder.
     */
    public Builder ingredientIds(final List<Integer> ingredientIds) {
      this.ingredientIds = ingredientIds;
      return this;
    }

    /**
     * Sets the fully populated ingredient resources used by the dish.
     *
     * @param ingredients the ingredient resources.
     * @return this builder.
     */
    public Builder ingredients(final List<Ingredient> ingredients) {
      this.ingredients = ingredients;
      return this;
    }

    /**
     * Sets the dish category name.
     *
     * @param category the category name.
     * @return this builder.
     */
    public Builder category(final String category) {
      this.category = category;
      return this;
    }

    /**
     * Builds a new {@link Dish} instance.
     *
     * @return the built dish.
     */
    public Dish build() {
      return new Dish(this);
    }
  }
}
