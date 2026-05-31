/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
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
 * JSON representation of an ingredient.
 *
 * @author SWAD Team
 */
public class Ingredient extends AbstractResource {
  private final Integer id;
  private final String name;
  private final List<Allergen> allergens;
  private final Boolean frozen;
  private final byte[] image;
  private final String imageType;

  /**
   * Creates a new {@code Ingredient} without image data.
   *
   * @param id the unique identifier of the ingredient.
   * @param name the name of the ingredient.
   * @param allergens the allergens associated with the ingredient.
   * @param frozen whether the ingredient is frozen.
   */
  public Ingredient(
      final Integer id, final String name, final List<Allergen> allergens, final Boolean frozen) {
    this(id, name, allergens, frozen, null, null);
  }

  /**
   * Creates a new {@code Ingredient}.
   *
   * @param id the unique identifier of the ingredient.
   * @param name the name of the ingredient.
   * @param allergens the allergens associated with the ingredient.
   * @param frozen whether the ingredient is frozen.
   * @param image the raw image bytes, or {@code null} if not present.
   * @param imageType the MIME type of the image (e.g. {@code image/webp}), or {@code null}.
   */
  public Ingredient(
      final Integer id,
      final String name,
      final List<Allergen> allergens,
      final Boolean frozen,
      final byte[] image,
      final String imageType) {
    this.id = id;
    this.name = name;
    this.allergens = allergens != null ? List.copyOf(allergens) : null;
    this.frozen = frozen;
    this.image = image;
    this.imageType = imageType;
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
   * @return a copy of the allergens list, or {@code null} if not specified.
   */
  public List<Allergen> getAllergens() {
    return allergens;
  }

  /**
   * Returns whether the ingredient is frozen.
   *
   * @return {@code true} if frozen, {@code false} if not frozen, or {@code null} if unspecified.
   */
  public Boolean isFrozen() {
    return frozen;
  }

  /**
   * Returns whether the ingredient is frozen.
   *
   * @return {@code true} if frozen, {@code false} if not frozen, or {@code null} if unspecified.
   */
  public Boolean getFrozen() {
    return frozen;
  }

  /** Returns the raw image bytes, or {@code null} if not present. */
  public byte[] getImage() {
    return image;
  }

  /** Returns the MIME type of the image, or {@code null} if not present. */
  public String getImageType() {
    return imageType;
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    if (id == null) jg.writeNullField("id");
    else jg.writeNumberField("id", id);

    if (name == null) jg.writeNullField("name");
    else jg.writeStringField("name", name);

    if (allergens == null) {
      jg.writeNullField("allergens");
    } else {
      jg.writeArrayFieldStart("allergens");
      for (final Allergen a : allergens) {
        jg.writeString(a.name());
      }
      jg.writeEndArray();
    }

    if (frozen == null) jg.writeNullField("is_frozen");
    else jg.writeBooleanField("is_frozen", frozen);

    jg.writeEndObject();

    jg.flush();
  }

  /**
   * Not supported. Ingredient create/edit endpoints now accept {@code multipart/form-data}; parse
   * fields with {@code req.getParameter()} and the image with {@code req.getPart("image")}.
   *
   * @throws UnsupportedOperationException always.
   */
  public static Ingredient fromJSON(final InputStream in)
      throws IOException, UnexpectedKeyException {
    throw new UnsupportedOperationException(
        "Ingredient endpoints accept multipart/form-data, not application/json.");
  }

  /**
   * Parses an {@code Ingredient} from a {@code multipart/form-data} request.
   *
   * <p>Expected fields: {@code name}, {@code is_frozen} (optional boolean), {@code allergens}
   * (multi-value), {@code id} (optional, used by edit endpoints). Optional file part: {@code
   * image}.
   *
   * @param req the HTTP request.
   * @return the parsed ingredient.
   * @throws IOException if an I/O error occurs reading the request.
   * @throws ServletException if the request is not a valid multipart request.
   * @throws MimeTypeParseException if an image part is present but its format is not recognised
   *     (accepted: webp, png, jpeg).
   * @throws IllegalArgumentException if an allergen value is not a valid {@link Allergen} constant.
   */
  public static Ingredient fromMultipart(final HttpServletRequest req)
      throws IOException, ServletException, MimeTypeParseException {
    Integer id = null;
    String name = null;
    Boolean isFrozen = null;
    final List<Allergen> allergens = new ArrayList<>();
    byte[] imageBytes = null;
    String imageType = null;

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

        case "is_frozen":
          try (InputStream is = p.getInputStream()) {
            final String val = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!val.isEmpty()) isFrozen = Boolean.parseBoolean(val);
          }
          break;

        case "allergens":
          try (InputStream is = p.getInputStream()) {
            final String val = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!val.isEmpty()) allergens.add(Allergen.valueOf(val));
          }
          break;

        case "image":
          imageType = p.getContentType();
          switch (imageType.toLowerCase().trim()) {
            case "image/webp":
            case "image/png":
            case "image/jpeg":
              break;
            default:
              throw new MimeTypeParseException(
                  String.format(
                      "Unsupported image format %s. Accepted: image/webp, image/png, image/jpeg.",
                      imageType));
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

    return new Ingredient(id, name, allergens, isFrozen, imageBytes, imageType);
  }
}
