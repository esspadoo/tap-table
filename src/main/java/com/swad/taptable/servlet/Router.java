package com.swad.taptable.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple router for dispatching HTTP requests to handlers based on method and path patterns. It
 * supports registering handlers for specific HTTP methods (GET, POST, PUT, DELETE) and path
 * patterns that can include path parameters (e.g. {@code /rest/user/{id}}). The router matches
 * incoming requests against registered routes in order and invokes the corresponding handler if a
 * match is found. If no route matches, the router returns {@code false} to indicate that the
 * request was not handled.
 *
 * @author SWAD Team
 */
public final class Router {

  private final List<Route> routes = new ArrayList<>();

  public Router get(String pattern, RouterHandler handler) {
    routes.add(new Route("GET", pattern, handler));
    return this;
  }

  public Router post(String pattern, RouterHandler handler) {
    routes.add(new Route("POST", pattern, handler));
    return this;
  }

  public Router put(String pattern, RouterHandler handler) {
    routes.add(new Route("PUT", pattern, handler));
    return this;
  }

  public Router delete(String pattern, RouterHandler handler) {
    routes.add(new Route("DELETE", pattern, handler));
    return this;
  }

  /**
   * Tries to match the incoming request against registered routes in order.
   *
   * @return {@code true} if a route matched and was handled, {@code false} otherwise.
   */
  public boolean dispatch(HttpServletRequest req, HttpServletResponse res) throws Exception {
    String method = req.getMethod();
    String path = req.getServletPath() + (req.getPathInfo() != null ? req.getPathInfo() : "");

    for (Route route : routes) {
      Map<String, String> params = route.match(method, path);
      if (params != null) {
        params.forEach((key, value) -> req.setAttribute(key, value));
        route.handler.handle(req, res);
        return true;
      }
    }

    return false;
  }

  /**
   * Note: this class is intentionally package-private since it's an implementation detail of
   * {@link Router}.
   *
   * <p>
   * Represents a registered route with a method, path pattern, and handler. The {@code match}
   * method checks if an incoming request matches this route and extracts path parameters if it
   * does.
   * </p>
   *
   * <p>
   * The path pattern is split into segments by {@code /}. Each segment can be a literal string or a
   * parameter (wrapped in braces). For example, the pattern {@code /rest/user/{id}} has three
   * segments: {@code "rest"}, {@code "user"}, and {@code "{id}"}. A request to
   * {@code /rest/user/123} would match this pattern and extract the parameter {@code id} with value
   * {@code 123}.
   * </p>
   */
  private static final class Route {
    /**
     * Note: constructor and methods don't have visibility modifiers since this class is
     * package-private and only used by {@link Router}.
     */
    private final String method;
    private final String[] segments;
    private final RouterHandler handler;

    /**
     * Creates a new route with the given method, path pattern, and handler.
     *
     * @param method HTTP method (e.g. "GET", "POST")
     * @param pattern URL pattern (e.g. "/rest/user/{id}")
     * @param handler handler to invoke if this route matches an incoming request
     */
    Route(String method, String pattern, RouterHandler handler) {
      this.method = method;
      this.segments = pattern.split("/", -1);
      this.handler = handler;
    }

    /**
     * Returns a map of extracted path parameters if this route matches, or {@code null} if not.
     */
    Map<String, String> match(String method, String path) {
      if (!this.method.equalsIgnoreCase(method)) {
        return null;
      }

      String[] pathSegments = path.split("/", -1);
      if (pathSegments.length != segments.length) {
        return null;
      }

      Map<String, String> params = new HashMap<>();
      for (int i = 0; i < segments.length; i++) {
        if (segments[i].startsWith("{") && segments[i].endsWith("}")) {
          params.put(segments[i].substring(1, segments[i].length() - 1), pathSegments[i]);
        } else if (!segments[i].equals(pathSegments[i])) {
          return null;
        }
      }
      return params;
    }
  }
}
