/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.user.ChangePasswordDAO;
import com.swad.taptable.dao.user.GetUserDAO;
import com.swad.taptable.resources.User;
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

public final class ProfileServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(ProfileServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_PROFILE);
    try {
      final int userId = (int) req.getAttribute("user_id");
      LogContext.setUser(String.valueOf(userId));

      final User user = new GetUserDAO(userId).access().getOutputParam();
      req.setAttribute("user", user);
      req.setAttribute("activePage", "profile");
      if ("true".equals(req.getParameter("success"))) {
        req.setAttribute("success", Boolean.TRUE);
      }

      res.setHeader("Cache-Control", "no-store");
      req.getRequestDispatcher("/jsp/dashboard/profile.jsp").forward(req, res);
    } catch (Exception e) {
      LOGGER.error("Error serving profile page: %s", e.getMessage());
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
      res.sendRedirect(req.getContextPath() + "/dashboard/profile?success=true");
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
      throws Exception {
    final int userId = (int) req.getAttribute("user_id");
    final User user = new GetUserDAO(userId).access().getOutputParam();
    req.setAttribute("user", user);
    req.setAttribute("activePage", "profile");
    req.setAttribute("error", message);
    res.setHeader("Cache-Control", "no-store");
    req.getRequestDispatcher("/jsp/dashboard/profile.jsp").forward(req, res);
  }
}
