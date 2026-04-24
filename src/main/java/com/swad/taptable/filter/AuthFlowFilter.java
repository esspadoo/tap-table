package com.swad.taptable.filter;

import com.swad.taptable.util.JWTUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter(urlPatterns = {"/login", "/register"}, filterName = "AuthFlowFilter",
    description = "Redirects already-authenticated users away to /dashboard")
public class AuthFlowFilter implements Filter {

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response,
      final FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
          try {
            JWTUtil.verify(cookie.getValue());
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
          } catch (Exception ignored) {
            break;
          }
        }
      }
    }

    chain.doFilter(request, response);
  }
}
