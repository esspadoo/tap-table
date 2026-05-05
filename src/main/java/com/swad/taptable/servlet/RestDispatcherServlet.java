/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.servlet;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swad.taptable.resources.Message;
import com.swad.taptable.resources.UserRole;
import com.swad.taptable.rest.category.CreateCategoryRR;
import com.swad.taptable.rest.category.DeleteCategoryRR;
import com.swad.taptable.rest.category.GetAllCategoriesRR;
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
import com.swad.taptable.rest.order.GetAllOrdersRR;
import com.swad.taptable.rest.order.GetOrderRR;
import com.swad.taptable.rest.order.GetUserOrdersRR;
import com.swad.taptable.rest.order.NewOrderRR;
import com.swad.taptable.rest.promotion.CheckPromotionRR;
import com.swad.taptable.rest.promotion.CheckPromotionUsageRR;
import com.swad.taptable.rest.promotion.GetPromotionsRR;
import com.swad.taptable.rest.promotion.NewPromotionRR;
import com.swad.taptable.rest.user.AuthenticateUserRR;
import com.swad.taptable.rest.user.DeleteUserRR;
import com.swad.taptable.rest.user.EditUserRR;
import com.swad.taptable.rest.user.GetUserRR;
import com.swad.taptable.rest.user.LogoutRR;
import com.swad.taptable.rest.user.RegisterUserRR;
import com.swad.taptable.util.ErrorCodes;
import com.swad.taptable.util.JWTUtil;
import com.swad.taptable.util.LogContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Single entry point for all REST requests. Resolves the caller's role from the JWT cookie, then
 * delegates routing and access control to {@link Router}.
 */
@WebServlet(name = "RestDispatcherServlet", urlPatterns = "/rest/*")
public final class RestDispatcherServlet extends HttpServlet {

  private static final Logger LOGGER =
      LogManager.getLogger(RestDispatcherServlet.class, StringFormatterMessageFactory.INSTANCE);

  private final Router router =
      new Router()
          // user
          .post(
              "/rest/user/login",
              (req, res) -> new AuthenticateUserRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .post(
              "/rest/user/logout",
              (req, res) -> new LogoutRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)
          .post(
              "/rest/user/register",
              (req, res) -> new RegisterUserRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .get(
              "/rest/user",
              (req, res) -> new GetUserRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)
          .put(
              "/rest/user",
              (req, res) -> new EditUserRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)
          .delete(
              "/rest/user",
              (req, res) -> new DeleteUserRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)

          // category
          .post(
              "/rest/category",
              (req, res) -> new CreateCategoryRR(req, res).serve(),
              RouteAccess.ADMIN_ONLY)
          .get(
              "/rest/category",
              (req, res) -> new GetAllCategoriesRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .delete(
              "/rest/category/{category_name}",
              (req, res) -> new DeleteCategoryRR(req, res).serve(),
              RouteAccess.ADMIN_ONLY)

          // ingredient
          .post(
              "/rest/ingredient",
              (req, res) -> new CreateIngredientRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .get(
              "/rest/ingredient",
              (req, res) -> new GetIngredientsRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .get(
              "/rest/ingredient/{ingredient_id}",
              (req, res) -> new GetIngredientRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .put(
              "/rest/ingredient",
              (req, res) -> new EditIngredientRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .delete(
              "/rest/ingredient/{ingredient_id}",
              (req, res) -> new DeleteIngredientRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)

          // dish
          .post(
              "/rest/dish",
              (req, res) -> new CreateDishRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .get("/rest/dish", (req, res) -> new GetDishesRR(req, res).serve(), RouteAccess.PUBLIC)
          .get(
              "/rest/dish/{dish_id}",
              (req, res) -> new GetDishRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .put(
              "/rest/dish",
              (req, res) -> new EditDishRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .delete(
              "/rest/dish/{dish_id}",
              (req, res) -> new DeleteDishRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)

          // order
          .post(
              "/rest/order",
              (req, res) -> new NewOrderRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)
          .get(
              "/rest/order",
              (req, res) -> new GetAllOrdersRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .get(
              "/rest/order/user",
              (req, res) -> new GetUserOrdersRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)
          .get(
              "/rest/order/{order_id}",
              (req, res) -> new GetOrderRR(req, res).serve(),
              RouteAccess.AUTHENTICATED)
          .put(
              "/rest/order/{order_id}/{order_status}",
              (req, res) -> new EditOrderStatusRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .delete(
              "/rest/order/{order_id}/",
              (req, res) -> new DeleteOrderRR(req, res).serve(),
              RouteAccess.ADMIN_ONLY)

          // promotion
          .post(
              "/rest/promotion",
              (req, res) -> new NewPromotionRR(req, res).serve(),
              RouteAccess.ADMIN_ONLY)
          .get(
              "/rest/promotion",
              (req, res) -> new GetPromotionsRR(req, res).serve(),
              RouteAccess.STAFF_OR_ADMIN)
          .get(
              "/rest/promotion/{promotion_code}",
              (req, res) -> new CheckPromotionRR(req, res).serve(),
              RouteAccess.PUBLIC)
          .get(
              "/rest/promotion/{promotion_code}/usage",
              (req, res) -> new CheckPromotionUsageRR(req, res).serve(),
              RouteAccess.AUTHENTICATED);

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse res)
      throws IOException {

    LogContext.setIPAddress(req.getRemoteAddr());
    final OutputStream out = res.getOutputStream();

    try {
      final Cookie[] cookies = req.getCookies();

      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (JWTUtil.COOKIE_NAME.equals(cookie.getName())) {
            try {
              DecodedJWT decoded = JWTUtil.verify(cookie.getValue());
              req.setAttribute("user_id", String.valueOf(decoded.getClaim("user_id").asInt()));
              UserRole role = UserRole.valueOf(decoded.getClaim("user_role").asString());
              req.setAttribute("user_role", role.name());
            } catch (JWTVerificationException e) {
              LOGGER.warn("Invalid or expired JWT, treating request as unauthenticated.");
            }
          }
        }
      }

      final DispatchResult result = router.dispatch(req, res);

      switch (result) {
        case OK:
          break;
        case NOT_FOUND:
          {
            LOGGER.warn("Unknown resource requested: %s.", req.getRequestURI());
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Message m =
                new Message(
                    "Unknown resource requested.",
                    ErrorCodes.UNKNOWN_RESOURCE_REQUESTED,
                    String.format("Requested resource is %s.", req.getRequestURI()));
            m.toJSON(out);
            break;
          }
        case UNAUTHORIZED:
          {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Message m =
                new Message("Authentication required.", ErrorCodes.MISSING_AUTH_COOKIE, null);
            m.toJSON(out);
            break;
          }
        case FORBIDDEN:
          {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Message m =
                new Message(
                    "You do not have permission to perform this operation.",
                    ErrorCodes.FORBIDDEN_OPERATION,
                    null);
            m.toJSON(out);
            break;
          }
      }

    } catch (Throwable t) {
      LOGGER.error("Unexpected error while processing the REST resource.", t);
      Message m = new Message("Unexpected error.", ErrorCodes.UNEXPECTED_ERROR, t.getMessage());
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
