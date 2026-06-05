/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.user.ChangePasswordDAO;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

public final class PasswordServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(PasswordServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_PASSWORD);
    try {
      req.setAttribute("activePage", "password");
      if ("true".equals(req.getParameter("success"))) {
        req.setAttribute("success", "Password changed successfully.");
      }
      res.setHeader("Cache-Control", "no-store");
      req.getRequestDispatcher("/jsp/dashboard/password.jsp").forward(req, res);
    } catch (Exception e) {
      LOGGER.error("Error serving password page: %s", e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.UPDATE_PASSWORD);
    try {
      final int userId = (int) req.getAttribute("user_id");
      LogContext.setUser(String.valueOf(userId));

      final String currentPassword = req.getParameter("current_password");
      final String newPassword = req.getParameter("new_password");
      final String confirmPassword = req.getParameter("confirm_password");

      if (newPassword == null || newPassword.length() < 8) {
        forwardWithError(req, res, "New password must be at least 8 characters.");
        return;
      }
      if (!newPassword.equals(confirmPassword)) {
        forwardWithError(req, res, "Passwords do not match.");
        return;
      }

      final boolean ok =
          new ChangePasswordDAO(userId, currentPassword, newPassword).access().getOutputParam();
      if (!ok) {
        forwardWithError(req, res, "Current password is incorrect.");
        return;
      }

      LOGGER.info("Password changed for user %d.", userId);
      res.sendRedirect(req.getContextPath() + "/dashboard/password?success=true");
    } catch (Exception e) {
      LOGGER.error("Error changing password: %s", e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }

  private void forwardWithError(
      final HttpServletRequest req, final HttpServletResponse res, final String message)
      throws ServletException, IOException {
    req.setAttribute("activePage", "password");
    req.setAttribute("error", message);
    res.setHeader("Cache-Control", "no-store");
    req.getRequestDispatcher("/jsp/dashboard/password.jsp").forward(req, res);
  }
}
