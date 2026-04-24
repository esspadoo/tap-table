package com.swad.taptable.rest.user;

import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Rest resource that handles user logout by expiring the JWT cookie
 *
 * @author SWAD Team
 */
public class LogoutRR extends AbstractRR {

  /**
   * Creates the REST resource that handles logout.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   */
  public LogoutRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.LOGOUT_USER, req, res);
  }

  /**
   * Expires the JWT cookie and returns {@code 204 No Content}.
   *
   * @throws IOException if the response cannot be written.
   */
  @Override
  protected void doServe() throws IOException {
    res.addHeader("Set-Cookie",
        JWTUtil.COOKIE_NAME + "=; Path=/; HttpOnly; SameSite=Strict; Max-Age=0");
    res.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }
}
