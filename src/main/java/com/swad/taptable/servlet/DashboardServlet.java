package com.swad.taptable.servlet;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.swad.taptable.dao.user.GetUserDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.JWTUtil;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.io.IOException;

public final class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER =
            LogManager.getLogger(DashboardServlet.class, StringFormatterMessageFactory.INSTANCE);

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
            throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.VIEW_DASHBOARD);
        try {
            final Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
                        try {
                            DecodedJWT decoded = JWTUtil.verify(cookie.getValue());
                            int userId = decoded.getClaim("user_id").asInt();
                            LogContext.setUser(String.valueOf(userId));
                            User user = new GetUserDAO(userId).access().getOutputParam();
                            req.setAttribute("user", user);
                            LOGGER.debug("Serving dashboard for user %d.", userId);
                            req.getRequestDispatcher("/jsp/dashboard/index.jsp").forward(req, res);
                        } catch (Exception e) {
                            LOGGER.warn(
                                    "Invalid or expired JWT during dashboard access, redirecting to login.",
                                    e);
                            res.sendRedirect(req.getContextPath() + "/login");
                        }
                        return;
                    }
                }
            }

            LOGGER.info("Unauthenticated dashboard access, redirecting to login.");
            res.sendRedirect(req.getContextPath() + "/login");
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
