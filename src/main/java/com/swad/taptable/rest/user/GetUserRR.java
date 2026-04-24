package com.swad.taptable.rest.user;

import com.swad.taptable.dao.user.GetUserDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.User;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles retrieval of a single user by ID
 *
 * @author SWAD Team
 */
public class GetUserRR extends AbstractRR {

    /**
     * Creates the REST resource that retrieves a single user.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     */
    public GetUserRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.GET_USER, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            final int userId = Integer.parseInt((String) req.getAttribute("user_id"));

            try {
                final User user = new GetUserDAO(userId).access().getOutputParam();

                if (user == null) {
                    LOGGER.warn("User with id %d not found.", userId);
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    new Message(String.format("User with id %d not found.", userId),
                            ErrorCodes.RESOURCE_NOT_FOUND, null).toJSON(res.getOutputStream());
                    return;
                }

                LOGGER.info("User with id %d successfully retrieved.", userId);
                res.setStatus(HttpServletResponse.SC_OK);
                user.toJSON(res.getOutputStream());

            } catch (final SQLException e) {
                LOGGER.error("Database error while retrieving user with id %d: %s", userId,
                        e.getMessage());
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                new Message("Database error while retrieving user.", ErrorCodes.UNEXPECTED_DB_ERROR,
                        e.getMessage()).toJSON(res.getOutputStream());
            }

        } catch (final NumberFormatException e) {
            LOGGER.warn("Invalid user ID format: %s", req.getAttribute("user_id"));
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Invalid user ID: " + req.getAttribute("user_id"),
                    ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage())
                            .toJSON(res.getOutputStream());
        }
    }
}
