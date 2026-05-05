/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.user.AuthenticateUserDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.JWTUtil;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

public final class LoginServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(LoginServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_LOGIN);
    try {
      req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.AUTHENTICATE_USER);
    try {
      final String email = req.getParameter("email");
      final String password = req.getParameter("password");

      try {
        final User user = new AuthenticateUserDAO(email, password).access().getOutputParam();

        if (user == null) {
          LOGGER.warn("Failed login attempt for email '%s'.", email);
          req.setAttribute("error", "Invalid email or password.");
          req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
          return;
        }

        LogContext.setUser(String.valueOf(user.getId()));
        LOGGER.info("User %d logged in successfully.", user.getId());

        final String token = JWTUtil.generateToken(user.getId(), user.getRole());
        res.addHeader(
            "Set-Cookie",
            JWTUtil.COOKIE_NAME
                + "="
                + token
                + "; Path=/; HttpOnly; SameSite=Strict; Max-Age="
                + JWTUtil.EXPIRY_SECONDS);
        res.sendRedirect(req.getContextPath() + "/dashboard");

      } catch (Exception e) {
        LOGGER.error("Unexpected error during login for email '%s'.", email, e);
        req.setAttribute("error", "An unexpected error occurred. Please try again.");
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
      }
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }
}
