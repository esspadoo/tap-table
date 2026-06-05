/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
 * Validates the JWT on protected routes by reading it from the {@code auth_token} HttpOnly cookie.
 * Redirects to {@code /login} if the token is missing or invalid. Sets the {@code user_id} request
 * attribute on success.
 */
@WebFilter(
    urlPatterns = {"/dashboard", "/dashboard/*"},
    filterName = "DashboardFilter",
    description = "Filter all dashboard URLs")
public class DashboardFilter implements Filter {

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
      req.setAttribute("user_id", decoded.getClaim("user_id").asInt());
      req.setAttribute("user_role", decoded.getClaim("user_role").asString());
    } catch (JWTVerificationException ex) {
      res.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    chain.doFilter(request, response);
  }

  /**
   * Extracts the JWT token from the auth cookie. Returns null if not found.
   * (https://www.reddit.com/r/node/comments/1hk3hs7/sending_jwt_token_via_cookies_vs_header/)
   *
   * @param cookies the array of cookies from the request
   * @return the token string if found, or null if not found
   */
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
