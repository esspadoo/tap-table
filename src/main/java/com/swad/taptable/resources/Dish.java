/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.swad.taptable.exception.NotValidDishException;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.awt.datatransfer.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
  private final byte[] image;
  private final ImageType imageType;

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
    this.image = builder.image;
    this.imageType = builder.imageType;
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
   * Returns the image associated with the dish, or {@code null} if not present.
   *
   * @return the image bytes, or {@code null} if not present.
   */
  public byte[] getImage() {
    return image;
  }

  /**
   * Returns the image type of the dish image, or {@code null} if no image is present.
   *
   * @return the image type.
   */
  public ImageType getImageType() {
    return imageType;
  }

  /**
   * Not supported. Dish create/edit endpoints now accept {@code multipart/form-data}; parse fields
   * with {@code req.getParameter()} and the image with {@code req.getPart("image")}.
   *
   * @throws UnsupportedOperationException always.
   */
  public static Dish fromJSON(final InputStream in)
      throws IOException, NotValidDishException, UnexpectedKeyException {
    throw new UnsupportedOperationException(
        "Dish endpoints accept multipart/form-data, not application/json.");
  }

  /**
   * Parses a {@code Dish} from a {@code multipart/form-data} request.
   *
   * @param req the HTTP request.
   * @return the parsed dish.
   * @throws IOException if an I/O error occurs reading the request.
   * @throws ServletException if the request is not a valid multipart request.
   * @throws MimeTypeParseException if an image part is present but its format is not recognised
   *     (accepted: webp, png, jpeg).
   */
  public static Dish fromMultipart(final HttpServletRequest req)
      throws IOException, ServletException, MimeTypeParseException {
    Integer id = null;
    String name = null;
    String description = null;
    Double price = null;
    String category = null;
    final List<Integer> ingredientIds = new ArrayList<>();
    byte[] imageBytes = null;
    ImageType imageType = null;

    for (final Part p : req.getParts()) {
      switch (p.getName()) {
        case "id":
          try (InputStream is = p.getInputStream()) {
            final String val = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!val.isEmpty()) id = Integer.parseInt(val);
          }
          break;

        case "name":
          try (InputStream is = p.getInputStream()) {
            name = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
          }
          break;

        case "description":
          try (InputStream is = p.getInputStream()) {
            description = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
          }
          break;

        case "price":
          try (InputStream is = p.getInputStream()) {
            final String val = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!val.isEmpty()) price = Double.parseDouble(val);
          }
          break;

        case "category":
          try (InputStream is = p.getInputStream()) {
            category = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
          }
          break;

        case "ingredient_ids":
          try (InputStream is = p.getInputStream()) {
            final String val = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!val.isEmpty()) ingredientIds.add(Integer.parseInt(val));
          }
          break;

        case "image":
          try {
            imageType = ImageType.fromMimeType(p.getContentType());
          } catch (final IllegalArgumentException e) {
            throw new MimeTypeParseException(
                String.format(
                    "Unsupported image format %s. Accepted: image/webp, image/png, image/jpeg.",
                    p.getContentType()));
          }
          try (InputStream is = p.getInputStream()) {
            imageBytes = is.readAllBytes();
            if (imageBytes.length == 0) {
              imageBytes = null;
              imageType = null;
            }
          }
          break;
      }
    }

    return new Dish.Builder()
        .id(id)
        .name(name)
        .description(description)
        .price(price)
        .category(category)
        .ingredientIds(ingredientIds)
        .image(imageBytes)
        .imageType(imageType)
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
    private byte[] image;
    private ImageType imageType;

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
     * Sets the raw image bytes of the dish.
     *
     * @param image the image data.
     * @return this builder.
     */
    public Builder image(final byte[] image) {
      this.image = image;
      return this;
    }

    /**
     * Sets the MIME type of the dish image.
     *
     * @param imageType the image type.
     * @return this builder.
     */
    public Builder imageType(final ImageType imageType) {
      this.imageType = imageType;
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
