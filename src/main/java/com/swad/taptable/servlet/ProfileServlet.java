/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

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
}
