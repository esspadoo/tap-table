/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a list of {@link Resource} objects.
 *
 * @author SWAD Team
 * @param <T> the type of resource contained in the list.
 */
public final class ResourceList<T extends Resource> extends AbstractResource {

  /** The list of resources. */
  private final Iterable<T> list;

  /**
   * Creates a list of resources.
   *
   * @param list the list of resources.
   */
  public ResourceList(final Iterable<T> list) {

    if (list == null) {
      LOGGER.error("Resource list cannot be null.");
      throw new NullPointerException("Resource list cannot be null.");
    }

    this.list = list;
  }

  /**
   * Returns the resources wrapped by this container.
   *
   * @return the wrapped resources.
   */
  public Iterable<T> getList() {
    return list;
  }

  @Override
  protected void writeJSON(final OutputStream out) throws IOException {

    final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

    jg.writeStartObject();

    jg.writeFieldName("resource-list");

    jg.writeStartArray();

    jg.flush();

    boolean firstElement = true;

    for (final Resource r : list) {

      // very bad work-around to add commas between resources
      if (firstElement) {
        r.toJSON(out);
        jg.flush();

        firstElement = false;
      } else {
        jg.writeRaw(',');
        jg.flush();

        r.toJSON(out);
        jg.flush();
      }
    }

    jg.writeEndArray();

    jg.writeEndObject();

    jg.flush();
  }
}
