package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.swad.taptable.exception.NotValidPromotionException;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * JSON representation of a promotion.
 *
 * @author SWAD Team
 */
public class Promotion extends AbstractResource {

  private final String code;

  private final String type;

  private final String description;

  private final LocalDateTime validFrom;

  private final LocalDateTime validTo;

  /**
   * Creates a new {@code Promotion}.
   *
   * @param code the unique promotion code.
   * @param type the type of the promotion.
   * @param description a human-readable description of the promotion.
   * @param validFrom the date and time from which the promotion is valid.
   * @param validTo the date and time until which the promotion is valid.
   * @throws NotValidPromotionException if {@code validTo} is not after {@code validFrom}.
   */
  public Promotion(final String code, final String type, final String description,
      final LocalDateTime validFrom, final LocalDateTime validTo)
      throws NotValidPromotionException {

    if (!validTo.isAfter(validFrom)) {
      throw new NotValidPromotionException("Promotion dates not valid.");
    }

    this.code = code;
    this.type = type;
    this.description = description;
    this.validFrom = validFrom;
    this.validTo = validTo;
  }

  /**
   * Returns the unique promotion code.
   *
   * @return the promotion code.
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the type of the promotion.
   *
   * @return the promotion type.
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the description of the promotion.
   *
   * @return the promotion description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the date and time from which the promotion is valid.
   *
   * @return the start of the validity period.
   */
  public LocalDateTime getValidFrom() {
    return validFrom;
  }

  /**
   * Returns the date and time until which the promotion is valid.
   *
   * @return the end of the validity period.
   */
  public LocalDateTime getValidTo() {
    return validTo;
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    jg.writeStringField("code", code);

    jg.writeStringField("type", type);

    jg.writeStringField("description", description);

    jg.writeStringField("valid_from", validFrom.toString());

    jg.writeStringField("valid_to", validTo.toString());

    jg.writeEndObject();

    jg.flush();
  }
}
