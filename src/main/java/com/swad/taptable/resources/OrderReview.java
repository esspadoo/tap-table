/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Resource representing a user's review of a dish in an order, containing the dish identifier and a
 * boolean indicating whether the user liked the dish or not.
 *
 * @author SWAD Team
 */
public class OrderReview extends AbstractResource {

  private final Integer dishId;
  private final Boolean isLiked;

  /**
   * Creates a new order review.
   *
   * @param dishId the identifier of the reviewed dish.
   * @param isLiked {@code true} if the user liked the dish, {@code false} otherwise.
   */
  public OrderReview(final Integer dishId, final Boolean isLiked) {
    this.dishId = dishId;
    this.isLiked = isLiked;
  }

  /**
   * Returns the identifier of the reviewed dish.
   *
   * @return the dish identifier.
   */
  public Integer getDishId() {
    return dishId;
  }

  /**
   * Returns whether the user liked the dish.
   *
   * @return {@code true} if liked, {@code false} if not liked, or {@code null} if not set.
   */
  public Boolean getIsLiked() {
    return isLiked;
  }

  /**
   * Parses an {@link OrderReview} from its JSON representation.
   *
   * @param in the input stream containing the JSON payload.
   * @return the parsed order review.
   * @throws IOException if an I/O error occurs while reading the stream.
   * @throws UnexpectedKeyException if the JSON contains an unrecognised field.
   */
  public static OrderReview fromJSON(final InputStream in)
      throws IOException, UnexpectedKeyException {
    final JsonParser jp = JSON_FACTORY.createParser(in);
    Integer dishId = null;
    Boolean isLiked = null;

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) continue;
      switch (jp.currentName()) {
        case "dish_id":
          jp.nextToken();
          dishId = jp.getIntValue();
          break;
        case "is_liked":
          jp.nextToken();
          isLiked = jp.getBooleanValue();
          break;
        default:
          throw new UnexpectedKeyException("Unexpected key: " + jp.currentName());
      }
    }

    return new OrderReview(dishId, isLiked);
  }

  @Override
  protected void writeJSON(final OutputStream out) throws Exception {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);
    jg.writeStartObject();
    if (dishId != null) jg.writeNumberField("dish_id", dishId);
    if (isLiked != null) jg.writeBooleanField("is_liked", isLiked);
    jg.writeEndObject();
    jg.flush();
  }
}
