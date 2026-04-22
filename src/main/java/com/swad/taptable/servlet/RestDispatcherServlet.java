package com.swad.taptable.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.user.AuthenticateUserRR;
import com.swad.taptable.rest.user.RegisterUserRR;
import com.swad.taptable.util.ErrorCodes;
import com.swad.taptable.util.LogContext;

/**
 * The main servlet responsible for dispatching REST requests to the appropriate
 * handlers.
 *
 * @author SWAD Team
 */
public final class RestDispatcherServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(RestDispatcherServlet.class,
            StringFormatterMessageFactory.INSTANCE);

    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    private final Router router = new Router()
            .post("/rest/user/login", (req, res) -> new AuthenticateUserRR(req, res).serve())
            .post("/rest/user/register", (req, res) -> new RegisterUserRR(req, res).serve());

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException {

        LogContext.setIPAddress(req.getRemoteAddr());
        final OutputStream out = res.getOutputStream();

        try {
            if (router.dispatch(req, res)) {
                return;
            }

            LOGGER.warn("Unknown resource requested: %s.", req.getRequestURI());
            final Message m = new Message("Unknown resource requested.", ErrorCodes.UNKNOWN_RESOURCE_REQUESTED,
                    String.format("Requested resource is %s.", req.getRequestURI()));
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(out);

        } catch (Throwable t) {
            LOGGER.error("Unexpected error while processing the REST resource.", t);
            final Message m = new Message("Unexpected error.", ErrorCodes.UNEXPECTED_ERROR, t.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(out);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
            LogContext.removeIPAddress();
        }
    }
}