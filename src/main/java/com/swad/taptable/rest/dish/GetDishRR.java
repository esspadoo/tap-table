package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.GetDishDAO;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class GetDishRR extends AbstractRR {

    public GetDishRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.GET_DISH, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            final int dishId = Integer.parseInt((String) req.getAttribute("dish_id"));

            final Dish out = new GetDishDAO(dishId).access().getOutputParam();

            if (out == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                new Message("Dish with id " + dishId + " not found.", ErrorCodes.RESOURCE_NOT_FOUND,
                        null).toJSON(res.getOutputStream());
                return;
            }

            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            out.toJSON(res.getOutputStream());

        } catch (final NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Invalid dish id format", ErrorCodes.INVALID_INPUT_PARAMETER,
                    e.getMessage()).toJSON(res.getOutputStream());
        } catch (final SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Message("Database error while retrieving dish.", ErrorCodes.UNEXPECTED_DB_ERROR,
                    e.getMessage()).toJSON(res.getOutputStream());
        }
    }
}
