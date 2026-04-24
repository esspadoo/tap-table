package com.swad.taptable.rest.category;

import com.swad.taptable.dao.category.GetAllCategoriesDAO;
import com.swad.taptable.resources.Category;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.resources.Message;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * REST resource that handles retrieval of all categories in the system
 *
 * @author SWAD Team
 */
public class GetAllCategoriesRR extends AbstractRR {

    /**
     * Creates the REST resource that retrieves all categories.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     */
    public GetAllCategoriesRR(final HttpServletRequest req, final HttpServletResponse res) {
        super(Actions.GET_CATEGORIES, req, res);
    }

    @Override
    protected void doServe() throws IOException {
        try {
            final ResourceList<Category> out = new GetAllCategoriesDAO().access().getOutputParam();

            res.setStatus(HttpServletResponse.SC_OK);
            out.toJSON(res.getOutputStream());

        } catch (final SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Message("Database error while retrieving categories.",
                    ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage()).toJSON(res.getOutputStream());
        }
    }
}
