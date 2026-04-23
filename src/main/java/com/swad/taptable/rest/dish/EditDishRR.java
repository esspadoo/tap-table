package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.EditDishDAO;
import com.swad.taptable.exception.NotValidDishException;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class EditDishRR extends AbstractRR {

    public EditDishRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.EDIT_DISH, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            final Dish in = Dish.fromJSON(req.getInputStream());

            if (in.getName() == null || in.getName().isBlank() || in.getPrice() == null
                    || in.getIngredientIds() == null || in.getIngredientIds().isEmpty()
                    || in.getCategory() == null || in.getId() == null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                new Message("Missing required fields", ErrorCodes.INVALID_INPUT_PARAMETER, null)
                        .toJSON(res.getOutputStream());
                return;
            }

            final Dish out = new EditDishDAO(in).access().getOutputParam();

            if (out == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                new Message("Dish with id " + in.getId() + " not found.",
                        ErrorCodes.RESOURCE_NOT_FOUND, null).toJSON(res.getOutputStream());
                return;
            }

            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            out.toJSON(res.getOutputStream());

        } catch (final NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Invalid dish id format", ErrorCodes.INVALID_INPUT_PARAMETER,
                    e.getMessage()).toJSON(res.getOutputStream());
        } catch (final NotValidDishException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Invalid dish data: " + e.getMessage(), ErrorCodes.INVALID_INPUT_PARAMETER,
                    null).toJSON(res.getOutputStream());
        } catch (final UnexpectedKeyException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Message("Malformed JSON in request body.", ErrorCodes.WRONG_RESOURCE_PROVIDED,
                    e.getMessage()).toJSON(res.getOutputStream());
        } catch (final SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Message("Database error while editing dish.", ErrorCodes.UNEXPECTED_DB_ERROR,
                    e.getMessage()).toJSON(res.getOutputStream());
        }
    }
}
