package com.swad.taptable.rest.category;

import com.swad.taptable.dao.category.CreateCategoryDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Category;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class CreateCategoryRR extends AbstractRR {

    public CreateCategoryRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.CREATE_CATEGORY, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            final Category in = Category.fromJSON(req.getInputStream());

            if (in.getName() == null || in.getName().isBlank()) {
                LOGGER.warn("Missing required field: name.");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                new Message("Missing required field: name must be provided.",
                        ErrorCodes.INVALID_INPUT_PARAMETER, null).toJSON(res.getOutputStream());
                return;
            }

            final Category out = new CreateCategoryDAO(in.getName()).access().getOutputParam();

            LOGGER.info("Category '%s' created.", out.getName());
            res.setStatus(HttpServletResponse.SC_CREATED);
            out.toJSON(res.getOutputStream());

        } catch (final UnexpectedKeyException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Malformed JSON in request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
                    e.getMessage()).toJSON(res.getOutputStream());
        } catch (final SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                LOGGER.warn("Category '%s' already exists.", e.getMessage());
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                new Message("Category already exists.", ErrorCodes.UNEXPECTED_DB_ERROR,
                        e.getMessage()).toJSON(res.getOutputStream());
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                new Message("Database error while creating category.",
                        ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage())
                                .toJSON(res.getOutputStream());
            }
        }
    }
}
