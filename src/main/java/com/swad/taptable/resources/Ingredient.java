package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON representation of an ingredient.
 *
 * @author SWAD Team
 */
public class Ingredient extends AbstractResource {

  private final Integer id;

  private final String name;

  private final List<Allergen> allergens;

  private final Boolean frozen;

  /**
   * Creates a new {@code Ingredient}.
   *
   * @param id the unique identifier of the ingredient.
   * @param name the name of the ingredient.
   * @param allergens the allergens associated with the ingredient; {@code null} or empty if none.
   * @param frozen whether the ingredient is frozen.
   */
  public Ingredient(final Integer id, final String name, final List<Allergen> allergens,
      final Boolean frozen) {
    this.id = id;
    this.name = name;
    this.allergens = allergens != null ? List.copyOf(allergens) : null;
    this.frozen = frozen;
  }

  /**
   * Returns the unique identifier of the ingredient.
   *
   * @return the id.
   */
  public Integer getId() {
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
  public Boolean isFrozen() {
    return frozen;
  }

  public Boolean getFrozen() {
    return frozen;
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

    if (allergens == null) {
      jg.writeNullField("allergens");
    } else {
      jg.writeArrayFieldStart("allergens");
      for (final Allergen a : allergens) {
        jg.writeString(a.name());
      }
      jg.writeEndArray();
    }

    if (frozen == null)
      jg.writeNullField("is_frozen");
    else
      jg.writeBooleanField("is_frozen", frozen);

    jg.writeEndObject();

    jg.flush();
  }

  /**
   * Parses a JSON input stream to create an instance of {@code Ingredient}
   *
   * <p>
   * The {@code allergens} array may be empty.
   * <p>
   * 
   * @param in the input stream containing the JSON payload.
   * @return a new {@code Ingredient} built from the parsed fields.
   * @throws IOException if there is an error reading from the stream or parsing the JSON.
   * @throws NotValidIngredientException if the payload contains unexpected fields, unknown allergen
   *         values, or is missing required fields.
   */
  public static Ingredient fromJSON(final InputStream in)
      throws IOException, UnexpectedKeyException {

    Integer jId = null;
    String jName = null;
    List<Allergen> jAllergens = null;
    Boolean jIsFrozen = null;

    try {
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
          case "allergens":
            if (jp.nextToken() == JsonToken.VALUE_NULL)
              break;

            jAllergens = new ArrayList<>();

            while (jp.nextToken() != JsonToken.END_ARRAY) {
              jAllergens.add(Allergen.valueOf(jp.getText()));
            }

            break;
          case "is_frozen":
            jp.nextToken();
            jIsFrozen = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getBooleanValue();
            break;
          default:
            throw new UnexpectedKeyException("Unexpected field: " + jp.currentName());
        }
      }
    } catch (IOException e) {
      LOGGER.error("Unable to parse an Ingredient object from JSON.", e);
      throw e;
    }

    return new Ingredient(jId, jName, jAllergens, jIsFrozen);
  }
}
