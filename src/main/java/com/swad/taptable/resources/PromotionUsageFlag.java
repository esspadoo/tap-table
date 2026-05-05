/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.OutputStream;

/**
 * Represents whether a promotion has already been used by a customer.
 *
 * @author SWAD Team
 */
public class PromotionUsageFlag extends AbstractResource {
  private final Boolean alreadyUsed;

  /**
   * Creates a new usage flag for a promotion.
   *
   * @param alreadyUsed whether the promotion has already been used.
   */
  public PromotionUsageFlag(final Boolean alreadyUsed) {
    this.alreadyUsed = alreadyUsed;
  }

  /**
   * Returns whether the promotion has already been used.
   *
   * @return {@code true} if the promotion has already been used, {@code false} if not used, or
   *     {@code null} if the information is unavailable.
   */
  public Boolean isUsed() {
    return alreadyUsed;
  }

  @Override
  protected void writeJSON(OutputStream out) throws Exception {
    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    jg.writeBooleanField("is_already_used", alreadyUsed);

    jg.writeEndObject();

    jg.flush();
  }
}
