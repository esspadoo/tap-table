package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import java.io.IOException;
import java.io.InputStream;
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
      final LocalDateTime validFrom, final LocalDateTime validTo) {
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

    if (code == null)
      jg.writeNullField("code");
    else
      jg.writeStringField("code", code);

    if (type == null)
      jg.writeNullField("type");
    else
      jg.writeStringField("type", type);

    if (description == null)
      jg.writeNullField("description");
    else
      jg.writeStringField("description", description);

    if (validFrom == null)
      jg.writeNullField("valid_from");
    else
      jg.writeStringField("valid_from", validFrom.toString());

    if (validTo == null)
      jg.writeNullField("valid_to");
    else
      jg.writeStringField("valid_to", validTo.toString());

    jg.writeEndObject();

    jg.flush();
  }

  public static Promotion fromJSON(final InputStream in)
      throws IOException, UnexpectedKeyException {
    final JsonParser jp = JSON_FACTORY.createParser(in);

    String jCode = null;
    String jType = null;
    String jDescription = null;
    LocalDateTime jValidFrom = null;
    LocalDateTime jValidTo = null;

    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
        continue;
      }

      switch (jp.currentName()) {
        case "code":
          jp.nextToken();
          jCode = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
          break;
        case "type":
          jp.nextToken();
          jType = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
          break;
        case "description":
          jp.nextToken();
          jDescription = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null : jp.getText();
          break;
        case "valid_from":
          jp.nextToken();
          jValidFrom = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null
              : LocalDateTime.parse(jp.getText());
          break;
        case "valid_to":
          jp.nextToken();
          jValidTo = jp.getCurrentToken() == JsonToken.VALUE_NULL ? null
              : LocalDateTime.parse(jp.getText());
          break;
        default:
          throw new UnexpectedKeyException("Unexpected field: " + jp.currentName());
      }
    }

    return new Promotion(jCode, jType, jDescription, jValidFrom, jValidTo);
  }
}
