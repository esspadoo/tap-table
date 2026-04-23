package com.swad.taptable.resources;

import java.io.OutputStream;
import com.fasterxml.jackson.core.JsonGenerator;

public class PromotionUsageFlag extends AbstractResource {

  private final Boolean alreadyUsed;

  public PromotionUsageFlag(final Boolean alreadyUsed) {
    this.alreadyUsed = alreadyUsed;
  }

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
