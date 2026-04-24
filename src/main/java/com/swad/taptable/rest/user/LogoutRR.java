package com.swad.taptable.rest.user;

import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LogoutRR extends AbstractRR {

    public LogoutRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.LOGOUT_USER, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        res.addHeader("Set-Cookie", JWTUtil.COOKIE_NAME + "=; Path=/; HttpOnly; SameSite=Strict; Max-Age=0");
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
