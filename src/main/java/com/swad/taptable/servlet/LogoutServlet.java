package com.swad.taptable.servlet;

import com.auth0.jwt.interfaces.DecodedJWT;
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

public final class LogoutServlet extends HttpServlet {

    private static final Logger LOGGER =
            LogManager.getLogger(LogoutServlet.class, StringFormatterMessageFactory.INSTANCE);

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
            throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.LOGOUT_USER);

        final Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        DecodedJWT decoded = JWTUtil.verify(cookie.getValue());
                        LogContext.setUser(String.valueOf(decoded.getClaim("user_id").asInt()));
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
        }

        try {
            LOGGER.info("User logged out.");
            res.addHeader("Set-Cookie",
                    JWTUtil.COOKIE_NAME + "=; Path=/; HttpOnly; SameSite=Strict; Max-Age=0");
            res.sendRedirect(req.getContextPath() + "/login");
        } catch (Exception e) {
            LOGGER.error("Error during logout: %s", e.getMessage());
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeUser();
        }
    }
}
