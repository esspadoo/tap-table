/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swad.taptable.resources.UserRole;
import com.swad.taptable.util.JWTUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Restricts access to staff/admin pages. Redirects unauthenticated requests to {@code /login} and
 * non-staff/admin requests to {@code /dashboard}.
 */
@WebFilter(
    urlPatterns = {
      "/dashboard/ingredient",
      "/dashboard/ingredient/*",
      "/dashboard/dishes",
      "/dashboard/dishes/*"
    },
    filterName = "StaffFilter",
    description = "Restricts access to staff and admin users only")
public class StaffFilter implements Filter {

  @Override
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String token = extractTokenFromCookies(req.getCookies());

    if (token == null) {
      res.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    try {
      DecodedJWT decoded = JWTUtil.verify(token);
      UserRole role = UserRole.valueOf(decoded.getClaim("user_role").asString());

      if (role != UserRole.STAFF && role != UserRole.ADMIN) {
        res.sendRedirect(req.getContextPath() + "/dashboard");
        return;
      }

      req.setAttribute("user_id", decoded.getClaim("user_id").asInt());
      req.setAttribute("user_role", role.name());
    } catch (JWTVerificationException ex) {
      res.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    chain.doFilter(request, response);
  }

  private String extractTokenFromCookies(final Cookie[] cookies) {
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
