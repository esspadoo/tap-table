package com.swad.taptable.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swad.taptable.util.JWTUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Validates the JWT on protected routes by reading it from the {@code auth_token} HttpOnly cookie.
 * Redirects to {@code /login} if the token is missing or invalid. Sets the {@code user_id} request
 * attribute on success.
 */
@WebFilter(urlPatterns = "/dashboard/*", filterName = "AuthFilter",
        description = "Filter all account transaction URLs")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String token = extractTokenFromCookies(req.getCookies());

        if (token == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            DecodedJWT decoded = JWTUtil.verify(token);
            int userId = decoded.getClaim("user_id").asInt();
            req.setAttribute("user_id", userId);
        } catch (JWTVerificationException ex) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the auth cookie. Returns null if not found.
     * (https://www.reddit.com/r/node/comments/1hk3hs7/sending_jwt_token_via_cookies_vs_header/)
     *
     * @param cookies the array of cookies from the request
     * @return the token string if found, or null if not found
     */
    private String extractTokenFromCookies(final Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
