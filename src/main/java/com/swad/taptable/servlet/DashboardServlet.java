/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.swad.taptable.dao.order.GetAllOrdersDAO;
import com.swad.taptable.dao.order.GetUserOrdersDAO;
import com.swad.taptable.dao.user.GetUserDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.resources.User;
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
 * Servlet handling the dashboard page, serving different views and data based on the user's role.
 *
 * @author SWAD Team
 */
public final class DashboardServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(DashboardServlet.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {
    LogContext.setIPAddress(req.getRemoteAddr());
    LogContext.setAction(Actions.VIEW_DASHBOARD);
    try {
      final int userId = (int) req.getAttribute("user_id");
      final UserRole role = UserRole.valueOf((String) req.getAttribute("user_role"));
      LogContext.setUser(String.valueOf(userId));

      final User user = new GetUserDAO(userId).access().getOutputParam();
      req.setAttribute("user", user);

      final ResourceList<Order> orderList =
          role == UserRole.CUSTOMER
              ? new GetUserOrdersDAO(userId).access().getOutputParam()
              : new GetAllOrdersDAO().access().getOutputParam();
      req.setAttribute("orders", orderList.getList());
      req.setAttribute("activePage", "overview");

      LOGGER.debug("Serving dashboard for user %d (%s).", userId, role);

      final String view =
          switch (role) {
            case CUSTOMER -> "/jsp/dashboard/customer.jsp";
            case STAFF -> "/jsp/dashboard/staff.jsp";
            case ADMIN -> "/jsp/dashboard/admin.jsp";
          };

      res.setHeader("Cache-Control", "no-store");
      req.getRequestDispatcher(view).forward(req, res);
    } catch (Exception e) {
      LOGGER.error("Error serving dashboard page: %s", e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      LogContext.removeIPAddress();
      LogContext.removeAction();
      LogContext.removeUser();
    }
  }
}
