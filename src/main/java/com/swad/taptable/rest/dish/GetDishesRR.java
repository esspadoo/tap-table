package com.swad.taptable.rest.dish;

import com.swad.taptable.dao.dish.GetDishesDAO;
import com.swad.taptable.resources.Dish;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.rest.AbstractRR;
import com.swad.taptable.util.Actions;
import com.swad.taptable.util.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class GetDishesRR extends AbstractRR {

  public GetDishesRR(final HttpServletRequest req, final HttpServletResponse res) {
    super(Actions.GET_DISHES, req, res);
  }

  @Override
  protected void doServe() throws IOException {
    try {
      final ResourceList<Dish> out = new GetDishesDAO().access().getOutputParam();

      res.setStatus(HttpServletResponse.SC_OK);
      out.toJSON(res.getOutputStream());

    } catch (final SQLException e) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      Message m = new Message("Database error while retrieving dishes.",
          ErrorCodes.UNEXPECTED_DB_ERROR, e.getMessage());
      m.toJSON(res.getOutputStream());
    }
  }
}
