package com.swad.taptable.servlet;

import com.swad.taptable.resources.UserRole;
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
 * incoming requests against registered routes in order and checks the caller's role against the
 * route's {@link RouteAccess} before invoking the handler.
 *
 * @author SWAD Team
 */
public final class Router {

  private final List<Route> routes = new ArrayList<>();

  public Router get(String pattern, RouteHandler handler, RouteAccess access) {
    routes.add(new Route("GET", pattern, handler, access));
    return this;
  }

  public Router post(String pattern, RouteHandler handler, RouteAccess access) {
    routes.add(new Route("POST", pattern, handler, access));
    return this;
  }

  public Router put(String pattern, RouteHandler handler, RouteAccess access) {
    routes.add(new Route("PUT", pattern, handler, access));
    return this;
  }

  public Router delete(String pattern, RouteHandler handler, RouteAccess access) {
    routes.add(new Route("DELETE", pattern, handler, access));
    return this;
  }

  /**
   * Tries to match the incoming request against registered routes in order. Checks the caller's
   * role before invoking the handler.
   *
   * @param callerRole the authenticated user's role, or {@code null} if unauthenticated.
   * @return the dispatch result: {@link DispatchResult#OK} if handled,
   *         {@link DispatchResult#NOT_FOUND} if no route matched,
   *         {@link DispatchResult#UNAUTHORIZED} if authentication is required but missing, or
   *         {@link DispatchResult#FORBIDDEN} if the role is insufficient.
   */
  public DispatchResult dispatch(HttpServletRequest req, HttpServletResponse res) throws Exception {
    String method = req.getMethod();
    String path = req.getServletPath() + (req.getPathInfo() != null ? req.getPathInfo() : "");

    String tmp = (String) req.getAttribute("user_role");
    UserRole role = tmp != null ? UserRole.valueOf(tmp) : null;

    for (Route route : routes) {
      Map<String, String> params = route.match(method, path);
      if (params != null) {
        DispatchResult access = hasAuthorization(route.access, role);
        if (access != DispatchResult.OK) {
          return access;
        }
        params.forEach((key, value) -> req.setAttribute(key, value));
        route.handler.handle(req, res);
        return DispatchResult.OK;
      }
    }

    return DispatchResult.NOT_FOUND;
  }

  private static DispatchResult hasAuthorization(RouteAccess access, UserRole role) {
    return switch (access) {
      case PUBLIC -> DispatchResult.OK;
      case AUTHENTICATED -> role != null ? DispatchResult.OK : DispatchResult.UNAUTHORIZED;
      case STAFF_OR_ADMIN -> role == null ? DispatchResult.UNAUTHORIZED
          : (role == UserRole.STAFF || role == UserRole.ADMIN) ? DispatchResult.OK
              : DispatchResult.FORBIDDEN;
      case ADMIN_ONLY -> role == null ? DispatchResult.UNAUTHORIZED
          : role == UserRole.ADMIN ? DispatchResult.OK : DispatchResult.FORBIDDEN;
    };
  }

  /**
   * Note: this class is intentionally package-private since it's an implementation detail of
   * {@link Router}.
   *
   * <p>
   * Represents a registered route with a method, path pattern, required access level, and handler.
   * </p>
   */
  private static final class Route {
    private final String method;
    private final String[] segments;
    private final RouteHandler handler;
    private final RouteAccess access;

    Route(String method, String pattern, RouteHandler handler, RouteAccess access) {
      this.method = method;
      this.segments = pattern.split("/", -1);
      this.handler = handler;
      this.access = access;
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
