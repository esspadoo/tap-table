/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.swad.taptable.dao.user.GetUserDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.JWTUtil;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

public final class DishServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(DishServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_DISHES);
    try {
      final Cookie[] cookies = req.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
            try {
              DecodedJWT decoded = JWTUtil.verify(cookie.getValue());
              int userId = decoded.getClaim("user_id").asInt();
              UserRole role = UserRole.valueOf(decoded.getClaim("user_role").asString());
              LogContext.setUser(String.valueOf(userId));

              if (role != UserRole.STAFF && role != UserRole.ADMIN) {
                LOGGER.info("User %d (%s) attempted to access dish management.", userId, role);
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
              }

              User user = new GetUserDAO(userId).access().getOutputParam();
              req.setAttribute("user", user);

              String pathInfo = req.getPathInfo();
              if (pathInfo != null && pathInfo.equals("/create")) {
                LOGGER.debug("Serving dish create page for user %d (%s).", userId, role);
                req.getRequestDispatcher("/jsp/dashboard/dish/create.jsp").forward(req, res);
              } else {
                LOGGER.debug("Serving dish list page for user %d (%s).", userId, role);
                req.getRequestDispatcher("/jsp/dashboard/dish/list.jsp").forward(req, res);
              }
            } catch (Exception e) {
              LOGGER.warn(
                  "Invalid or expired JWT during dish dashboard access, redirecting to login.", e);
              res.sendRedirect(req.getContextPath() + "/login");
            }
            return;
          }
        }
      }

      LOGGER.info("Unauthenticated dish dashboard access, redirecting to login.");
      res.sendRedirect(req.getContextPath() + "/login");
    } catch (Exception e) {
      LOGGER.error("Error serving dish dashboard page: %s", e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }
}