package com.swad.taptable.rest.ingredient;

import java.io.IOException;
import java.sql.SQLException;
import com.swad.taptable.dao.ingredient.CreateIngredientDAO;
import com.swad.taptable.exception.json.UnexpectedKeyException;
import com.swad.taptable.resources.Ingredient;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Rest resource for creating a new ingredient.
 * 
 * <p>
 * The ingredient data is provided in the request body as a JSON object. The required fields are
 * {@code name} and {@code isFrozen}. If any of these fields is missing or invalid, the response
 * status is set to {@code 400} (Bad Request).
 * </p>
 * 
 * <p>
 * If the ingredient is successfully created, the response status is set to {@code 201} (Created)
 * and the created ingredient is returned in the response body as a JSON object.
 * </p>
 * 
 * <p>
 * If any server side error occurs (e.g. database) during the creation process, the response status
 * is set to {@code 500} (Internal Server Error) and an error message is returned in the response
 * body as a JSON object with the appropriate error code and message details.
 * </p>
 * 
 * @author SWAD Team
 */
public class CreateIngredientRR extends AbstractRR {

    /**
     * Creates a new {@code NewIngredientRR} object.
     * 
     * @param req the HTTP request
     * @param res the HTTP response
     */
    public CreateIngredientRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.CREATE_INGREDIENT, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            // Parse the ingredient from the request body
            final Ingredient in = Ingredient.fromJSON(req.getInputStream());

            // Check that the required fields provided are not null nor empty
            if (in.getName() == null || in.isFrozen() == null) {
                LOGGER.warn("Invalid input for creating ingredient: missing required fields");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Message m =
                        new Message("Missing required fields: name and isFrozen must be provided",
                                ErrorCodes.INVALID_INPUT_PARAMETER, null);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
                return;
            }

            final CreateIngredientDAO dao = new CreateIngredientDAO(in);

            Ingredient out = dao.access().getOutputParam();

            if (out == null) {
                LOGGER.warn("Failed to create ingredient %s", in.getName());
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                Message m = new Message("Failed to create ingredient.",
                        ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
                return;
            }

            LOGGER.debug("Ingredient %s created with id %d", out.getName(), out.getId());
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            out.toJSON(res.getOutputStream());
        } catch (final UnexpectedKeyException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Message m = new Message("Malformed JSON in request body.",
                    ErrorCodes.WRONG_RESOURCE_PROVIDED, e.getMessage());
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (final SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Message m = new Message("Database error while creating ingredient.",
                    ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }

}
