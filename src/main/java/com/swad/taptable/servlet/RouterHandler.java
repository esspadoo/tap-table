package com.swad.taptable.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.swad.taptable.resources.Resource;

/**
 * <p>
 * Functional interface representing a handler for a specific route in the REST API. It takes an
 * HTTP request and response as parameters and can throw an exception.
 * </p>
 *
 * <p>
 * This interface is used in the {@link Router} to define the logic for handling specific routes.
 * Instead of using Bi-Consumers, we use this functional interface to allow for checked exceptions,
 * which can be useful for handling various error scenarios in a more flexible way.
 * </p>
 *
 * @author SWAD Team
 */
@FunctionalInterface
public interface RouterHandler {
  /**
   * Handles a REST request for a specific route. Implementations of this method should contain the
   * logic for serving {@link Resource#serve()} the specific
   *
   * @param req the HTTP request
   * @param res the response to be sent back to the client
   * @throws Exception if any (known) error occurs while processing the request
   */
  void handle(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
