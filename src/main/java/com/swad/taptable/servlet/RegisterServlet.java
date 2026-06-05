/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.user.RegisterUserDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.LogContext;
import com.swad.taptable.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

public final class RegisterServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(RegisterServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_REGISTER);
    try {
      req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.REGISTER_USER);
    try {
      final String name = req.getParameter("name");
      final String surname = req.getParameter("surname");
      final String username = req.getParameter("username");
      final String email = req.getParameter("email");
      final String phoneNumber = req.getParameter("phone_number");
      final String password = req.getParameter("password");
      final String confirmPassword = req.getParameter("confirm_password");

      if (!password.equals(confirmPassword)) {
        LOGGER.warn("Registration failed: passwords do not match for username '%s'.", username);
        req.setAttribute("error", "Passwords do not match.");
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
        return;
      }

      if (!Validator.isValidPassword(password)) {
        LOGGER.warn(
            "Registration failed: password does not meet policy for username '%s'.", username);
        req.setAttribute(
            "error",
            "Password must be 8-16 characters and contain at least one uppercase letter, one"
                + " lowercase letter, one digit, and one special character.");
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
        return;
      }

      final User user =
          new User.Builder()
              .name(name)
              .surname(surname)
              .username(username)
              .email(email)
              .phoneNumber(phoneNumber)
              .password(password)
              .build();

      try {
        new RegisterUserDAO(user).access();
        LOGGER.info("User '%s' registered successfully.", username);
        res.sendRedirect(req.getContextPath() + "/login");
      } catch (SQLException e) {
        if ("23505".equals(e.getSQLState())) {
          LOGGER.warn(
              "Registration conflict: username or email already in use for '%s'.", username);
          req.setAttribute("error", "Username or email already in use.");
        } else {
          LOGGER.error("Unexpected database error during registration for '%s'.", username, e);
          req.setAttribute("error", "An unexpected error occurred. Please try again.");
        }
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
      } catch (Exception e) {
        LOGGER.error("Unexpected error during registration for '%s'.", username, e);
        req.setAttribute("error", "An unexpected error occurred. Please try again.");
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
      }
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
    }
  }
}
