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
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "NavbarContextFilter")
public class NavbarContextFilter implements Filter {

  @Override
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    boolean authenticated = false;
    String role = null;

    final Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
          try {
            DecodedJWT jwt = JWTUtil.verify(cookie.getValue());
            authenticated = true;
            role = jwt.getClaim("user_role").asString();
          } catch (JWTVerificationException ignored) {
          }
          break;
        }
      }
    }

    req.setAttribute("nav_authenticated", authenticated);
    req.setAttribute("nav_role", role);

    chain.doFilter(request, response);
  }
}
