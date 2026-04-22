package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.NotValidIngredientException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JSON representation of an ingredient.
 *
 * @author SWAD Team
 */
public class Ingredient extends AbstractResource {

  private final int id;

  private final String name;

  private final List<Allergen> allergens;

  private final boolean isFrozen;

  /**
   * Creates a new {@code Ingredient}.
   *
   * @param id the unique identifier of the ingredient.
   * @param name the name of the ingredient.
   * @param allergens the allergens associated with the ingredient; {@code null} or empty if none.
   * @param isFrozen whether the ingredient is frozen.
   */
  public Ingredient(final int id, final String name, final List<Allergen> allergens,
      final boolean isFrozen) {
    this.id = id;
    this.name = name;
    this.allergens = allergens != null ? Collections.unmodifiableList(allergens) : List.of();
    this.isFrozen = isFrozen;
  }

  /**
   * Returns the unique identifier of the ingredient.
   *
   * @return the id.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the name of the ingredient.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the allergens associated with the ingredient.
   *
   * @return an unmodifiable list of allergens; empty if none.
   */
  public List<Allergen> getAllergens() {
    return allergens;
  }

  /**
   * Returns whether the ingredient is frozen.
   *
   * @return {@code true} if frozen, {@code false} otherwise.
   */
  public boolean isFrozen() {
    return isFrozen;
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    jg.writeNumberField("id", id);

    jg.writeStringField("name", name);

    jg.writeArrayFieldStart("allergens");
    for (final Allergen a : allergens) {
      jg.writeString(a.name());
    }
    jg.writeEndArray();

    jg.writeBooleanField("is_frozen", isFrozen);

    jg.writeEndObject();

    jg.flush();
  }

  /**
   * Parses a JSON input stream to create an instance of {@code Ingredient}. The expected JSON
   * format mirrors the output of {@link #writeJSON}, i.e.:
   *
   * <pre>
   * { "id": 1, "name": "flour", "allergens": ["cereals", "eggs"], "is_frozen": false }
   * </pre>
   *
   * The {@code allergens} array may be empty.
   *
   * @param in the input stream containing the JSON payload.
   * @return a new {@code Ingredient} built from the parsed fields.
   * @throws IOException if there is an error reading from the stream or parsing the JSON.
   * @throws NotValidIngredientException if the payload contains unexpected fields, unknown allergen
   *         values, or is missing required fields.
   */
  public static Ingredient fromJson(final InputStream in)
      throws IOException, NotValidIngredientException {

    int jId = -1;
    String jName = null;
    final List<Allergen> jAllergens = new ArrayList<>();
    boolean jIsFrozen = false;
    boolean idSet = false;
    boolean frozenSet = false;

    try {
      final JsonParser jp = JSON_FACTORY.createParser(in);

      while (jp.nextToken() != JsonToken.END_OBJECT) {
        if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
          continue;
        }

        switch (jp.currentName()) {
          case "id":
            jp.nextToken();
            jId = jp.getIntValue();
            idSet = true;
            break;
          case "name":
            jp.nextToken();
            jName = jp.getText();
            break;
          case "allergens":
            jp.nextToken(); // START_ARRAY
            while (jp.nextToken() != JsonToken.END_ARRAY) {
              if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
                continue;
              }
              try {
                jAllergens.add(Allergen.valueOf(jp.getText()));
              } catch (IllegalArgumentException e) {
                throw new NotValidIngredientException("Unknown allergen: " + jp.getText());
              }
            }
            break;
          case "is_frozen":
            jp.nextToken();
            jIsFrozen = jp.getBooleanValue();
            frozenSet = true;
            break;
          default:
            LOGGER.warn("Unexpected field in ingredient payload: %s", jp.currentName());
            throw new NotValidIngredientException("Unexpected field: " + jp.currentName());
        }
      }
    } catch (NotValidIngredientException e) {
      LOGGER.error("Not valid ingredient payload: ", e);
      throw e;
    } catch (IOException e) {
      LOGGER.error("Unable to parse an Ingredient object from JSON.", e);
      throw e;
    }

    if (!idSet || jName == null || !frozenSet) {
      throw new NotValidIngredientException("Missing required fields in ingredient payload.");
    }

    return new Ingredient(jId, jName, jAllergens, jIsFrozen);
  }
}
