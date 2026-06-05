/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.user.AdminDeleteUserDAO;
import com.swad.taptable.dao.user.ChangeUserRoleDAO;
import com.swad.taptable.dao.user.GetAllUsersDAO;
import com.swad.taptable.resources.UserRole;
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

/**
 * Servlet handling the user management page
 *
 * @author SWAD Team
 */
public final class UsersServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(UsersServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_USERS);
    try {
      final UserRole role = UserRole.valueOf((String) req.getAttribute("user_role"));
      if (role != UserRole.ADMIN) {
        res.sendRedirect(req.getContextPath() + "/dashboard");
        return;
      }

      final int userId = (int) req.getAttribute("user_id");
      LogContext.setUser(String.valueOf(userId));

      req.setAttribute("users", new GetAllUsersDAO().access().getOutputParam().getList());
      req.setAttribute("activePage", "users");

      res.setHeader("Cache-Control", "no-store");
      req.getRequestDispatcher("/jsp/dashboard/users.jsp").forward(req, res);
    } catch (Exception e) {
      LOGGER.error("Error serving users page: %s", e.getMessage());
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
    try {
      final UserRole role = UserRole.valueOf((String) req.getAttribute("user_role"));
      if (role != UserRole.ADMIN) {
        res.sendRedirect(req.getContextPath() + "/dashboard");
        return;
      }

      final int adminId = (int) req.getAttribute("user_id");
      LogContext.setUser(String.valueOf(adminId));

      final String action = req.getParameter("_action");
      final String userIdParam = req.getParameter("userId");
      if (userIdParam == null || userIdParam.isBlank()) {
        res.sendRedirect(req.getContextPath() + "/dashboard/users");
        return;
      }

      int targetUserId;
      try {
        targetUserId = Integer.parseInt(userIdParam);
      } catch (NumberFormatException e) {
        res.sendRedirect(req.getContextPath() + "/dashboard/users");
        return;
      }

      if (targetUserId == adminId) {
        LOGGER.warn("Admin %d attempted to modify their own account.", adminId);
        res.sendRedirect(req.getContextPath() + "/dashboard/users");
        return;
      }

      if ("change_role".equals(action)) {
        LogContext.setAction(Actions.CHANGE_USER_ROLE);
        final UserRole newRole = UserRole.valueOf(req.getParameter("newRole"));
        if (newRole == UserRole.ADMIN) {
          LOGGER.warn("Admin %d attempted to promote user %d to ADMIN.", adminId, targetUserId);
          res.sendRedirect(req.getContextPath() + "/dashboard/users");
          return;
        }
        new ChangeUserRoleDAO(targetUserId, newRole).access();
        LOGGER.info("Admin %d changed role of user %d to %s.", adminId, targetUserId, newRole);
      } else if ("delete_user".equals(action)) {
        LogContext.setAction(Actions.ADMIN_DELETE_USER);
        new AdminDeleteUserDAO(targetUserId).access();
        LOGGER.info("Admin %d deleted user %d.", adminId, targetUserId);
      }

      res.sendRedirect(req.getContextPath() + "/dashboard/users");
    } catch (Exception e) {
      LOGGER.error("Error handling user management action: %s", e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }
}
