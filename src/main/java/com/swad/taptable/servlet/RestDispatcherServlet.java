package com.swad.taptable.servlet;

import com.swad.taptable.rest.dish.CreateDishRR;
import com.swad.taptable.rest.dish.DeleteDishRR;
import com.swad.taptable.rest.dish.EditDishRR;
import com.swad.taptable.rest.dish.GetDishRR;
import com.swad.taptable.rest.dish.GetDishesRR;
import com.swad.taptable.rest.ingredient.CreateIngredientRR;
import com.swad.taptable.rest.ingredient.DeleteIngredientRR;
import com.swad.taptable.rest.ingredient.EditIngredientRR;
import com.swad.taptable.rest.ingredient.GetIngredientRR;
import com.swad.taptable.rest.ingredient.GetIngredientsRR;
import com.swad.taptable.rest.order.DeleteOrderRR;
import com.swad.taptable.rest.order.EditOrderStatusRR;
import com.swad.taptable.rest.order.GetOrderRR;
import com.swad.taptable.rest.order.GetUserOrdersRR;
import com.swad.taptable.rest.order.NewOrderRR;
import com.swad.taptable.rest.promotion.CheckPromotionRR;
import com.swad.taptable.rest.promotion.CheckPromotionUsageRR;
import com.swad.taptable.rest.promotion.GetPromotionsRR;
import com.swad.taptable.rest.promotion.NewPromotionRR;
import com.swad.taptable.rest.user.AuthenticateUserRR;
import com.swad.taptable.rest.user.RegisterUserRR;
import com.swad.taptable.resources.Message;
import com.swad.taptable.util.ErrorCodes;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Single entry point for all REST requests. Delegates routing to {@link Router}.
 */
@WebServlet(name = "RestDispatcherServlet", urlPatterns = "/rest/*")
public final class RestDispatcherServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(RestDispatcherServlet.class, StringFormatterMessageFactory.INSTANCE);

  private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

  private final Router router = new Router()
      // user
      .post("/rest/user/login", (req, res) -> new AuthenticateUserRR(req, res).serve())
      .post("/rest/user/register", (req, res) -> new RegisterUserRR(req, res).serve())

      // ingredient
      .post("/rest/ingredient", (req, res) -> new CreateIngredientRR(req, res).serve())
      .get("/rest/ingredient", (req, res) -> new GetIngredientsRR(req, res).serve())
      .get("/rest/ingredient/{ingredient_id}", (req, res) -> new GetIngredientRR(req, res).serve())
      .put("/rest/ingredient", (req, res) -> new EditIngredientRR(req, res).serve())
      .delete("/rest/ingredient/{ingredient_id}",
          (req, res) -> new DeleteIngredientRR(req, res).serve())

      // dish
      .post("/rest/dish", (req, res) -> new CreateDishRR(req, res).serve())
      .get("/rest/dish", (req, res) -> new GetDishesRR(req, res).serve())
      .get("/rest/dish/{dish_id}", (req, res) -> new GetDishRR(req, res).serve())
      .put("/rest/dish", (req, res) -> new EditDishRR(req, res).serve())
      .delete("/rest/dish/{dish_id}", (req, res) -> new DeleteDishRR(req, res).serve())

      // order
      .post("/rest/order", (req, res) -> new NewOrderRR(req, res).serve())
      .get("/rest/order/{order_id}", (req, res) -> new GetOrderRR(req, res).serve())
      .get("/rest/order/user/{user_id}", (req, res) -> new GetUserOrdersRR(req, res).serve())
      .put("/rest/order/{order_id}/{order_status}",
          (req, res) -> new EditOrderStatusRR(req, res).serve())
      .delete("/rest/order/{order_id}/", (req, res) -> new DeleteOrderRR(req, res).serve())

      // promotion
      .post("/rest/promotion", (req, res) -> new NewPromotionRR(req, res).serve())
      .get("/rest/promotion", (req, res) -> new GetPromotionsRR(req, res).serve())
      .get("/rest/promotion/{promotion_code}", (req, res) -> new CheckPromotionRR(req, res).serve())
      .get("/rest/promotion/{promotion_code}/usage/{user_id}",
          (req, res) -> new CheckPromotionUsageRR(req, res).serve());

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
      final Message m =
          new Message("Unknown resource requested.", ErrorCodes.UNKNOWN_RESOURCE_REQUESTED,
              String.format("Requested resource is %s.", req.getRequestURI()));
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.setContentType(JSON_UTF_8_MEDIA_TYPE);
      m.toJSON(out);

    } catch (Throwable t) {
      LOGGER.error("Unexpected error while processing the REST resource.", t);
      final Message m =
          new Message("Unexpected error.", ErrorCodes.UNEXPECTED_ERROR, t.getMessage());
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