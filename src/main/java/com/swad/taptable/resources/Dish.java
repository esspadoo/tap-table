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

  private Dish(final Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.price = builder.price;
    this.ingredientIds = builder.ingredientIds;
    this.ingredients = builder.ingredients;
    this.category = builder.category;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Double getPrice() {
    return price;
  }

  public List<Integer> getIngredientIds() {
    return ingredientIds;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public String getCategory() {
    return category;
  }

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
            jIngredientIds
                .add(jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getIntValue());
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

    return new Dish.Builder().id(jId).name(jName).description(jDescription).price(jPrice)
        .ingredientIds(jIngredientIds).category(jCategory).build();
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    if (id == null)
      jg.writeNullField("id");
    else
      jg.writeNumberField("id", id);

    if (name == null)
      jg.writeNullField("name");
    else
      jg.writeStringField("name", name);

    if (description == null)
      jg.writeNullField("description");
    else
      jg.writeStringField("description", description);

    if (price == null)
      jg.writeNullField("price");
    else
      jg.writeNumberField("price", price);

    if (ingredients == null) {
      jg.writeNullField("ingredients");
    } else {
      jg.writeArrayFieldStart("ingredients");
      for (final Ingredient ingredient : ingredients) {
        jg.writeStartObject();

        if (ingredient.getId() == null)
          jg.writeNullField("id");
        else
          jg.writeNumberField("id", ingredient.getId());

        if (ingredient.getName() == null)
          jg.writeNullField("name");
        else
          jg.writeStringField("name", ingredient.getName());

        if (ingredient.getAllergens() == null) {
          jg.writeNullField("allergens");
        } else {
          jg.writeArrayFieldStart("allergens");
          for (final Allergen a : ingredient.getAllergens()) {
            jg.writeString(a.name());
          }
          jg.writeEndArray();
        }

        if (ingredient.isFrozen() == null)
          jg.writeNullField("is_frozen");
        else
          jg.writeBooleanField("is_frozen", ingredient.isFrozen());

        jg.writeEndObject();
      }
      jg.writeEndArray();
    }

    if (category == null)
      jg.writeNullField("category");
    else
      jg.writeStringField("category", category);

    jg.writeEndObject();

    jg.flush();
  }

  public static class Builder {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private List<Integer> ingredientIds;
    private List<Ingredient> ingredients;
    private String category;

    public Builder id(final Integer id) {
      this.id = id;
      return this;
    }

    public Builder name(final String name) {
      this.name = name;
      return this;
    }

    public Builder description(final String description) {
      this.description = description;
      return this;
    }

    public Builder price(final Double price) {
      this.price = price;
      return this;
    }

    public Builder ingredientIds(final List<Integer> ingredientIds) {
      this.ingredientIds = ingredientIds;
      return this;
    }

    public Builder ingredients(final List<Ingredient> ingredients) {
      this.ingredients = ingredients;
      return this;
    }

    public Builder category(final String category) {
      this.category = category;
      return this;
    }

    public Dish build() {
      return new Dish(this);
    }
  }
}