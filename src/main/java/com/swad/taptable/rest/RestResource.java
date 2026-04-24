package com.swad.taptable.rest;

import java.io.IOException;


/**
 * Represents a generic REST resource. The {@link #serve()} method handles the request served by
 * this REST resource.
 *
 * @author SWAD Team
 */
public interface RestResource {

  /**
   * Serves a REST request.
   *
   * @throws IOException if any error occurs in the client/server communication.
   */
  public void serve() throws IOException;
}
