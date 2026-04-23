package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.DeleteDishDAO;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteDishRR extends AbstractRR {

    public DeleteDishRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.DELETE_DISH, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            final int dishId = Integer.parseInt((String) req.getAttribute("dish_id"));

            final boolean deleted = new DeleteDishDAO(dishId).access().getOutputParam();

            if (!deleted) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                new Message("Dish with id " + dishId + " not found.", ErrorCodes.RESOURCE_NOT_FOUND,
                        null).toJSON(res.getOutputStream());
                return;
            }

            res.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (final NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Invalid dish id format", ErrorCodes.INVALID_INPUT_PARAMETER,
                    e.getMessage()).toJSON(res.getOutputStream());
        } catch (final SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Message("Database error while deleting dish.", ErrorCodes.UNEXPECTED_DB_ERROR,
                    e.getMessage()).toJSON(res.getOutputStream());
        }
    }
}
