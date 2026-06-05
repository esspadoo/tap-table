/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderDish;
import com.swad.taptable.resources.OrderStatus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderDAO extends AbstractDAO<Order> {
  private static final String GET_DISH_PRICE_STATEMENT = "SELECT price FROM dishes WHERE id = ?";
  private static final String CREATE_ORDER_STATEMENT =
      "INSERT INTO orders (user_id, promotion_id, total_amount, status) VALUES (?, ?, ?, ?::order_status) RETURNING *";
  private static final String CREATE_ORDER_DISHES_STATEMENT =
      "INSERT INTO order_dishes (order_id, dish_id, quantity, is_liked) VALUES (?, ?, ?, ?) RETURNING *";

  private final Order order;
  private final Integer userId;

  public CreateOrderDAO(final Order order, final Integer userId) {
    this.order = order;
    this.userId = userId;
  }

  @Override
  protected void doAccess() throws Exception {
    Order o = null;
    con.setAutoCommit(false);

    try {
      float total = 0f;
      try (PreparedStatement priceStmt = con.prepareStatement(GET_DISH_PRICE_STATEMENT)) {
        for (OrderDish od : order.getDishes()) {
          priceStmt.setInt(1, od.getDishId());
          try (ResultSet rs = priceStmt.executeQuery()) {
            if (!rs.next()) {
              LOGGER.warn("Dish with id %d not found when creating order.", od.getDishId());
              return;
            }
            total += rs.getFloat("price") * od.getQuantity();
          }
        }
      }

      try (PreparedStatement createOrderStmt = con.prepareStatement(CREATE_ORDER_STATEMENT)) {
        createOrderStmt.setInt(1, this.userId);
        if (order.getPromotionId() == null) {
          createOrderStmt.setNull(2, java.sql.Types.INTEGER);
        } else {
          createOrderStmt.setInt(2, order.getPromotionId());
        }
        createOrderStmt.setFloat(3, total);
        createOrderStmt.setString(4, order.getStatus().name());

        try (ResultSet orderRs = createOrderStmt.executeQuery()) {
          if (!orderRs.next()) {
            throw new SQLException("Failed to create order");
          }

          Integer orderId = orderRs.getInt("id");
          Integer userId = orderRs.getInt("user_id");
          Integer promotionId = orderRs.getObject("promotion_id", Integer.class);
          Float totalPrice = orderRs.getFloat("total_amount");
          OrderStatus status = OrderStatus.valueOf(orderRs.getString("status"));

          try (PreparedStatement createOrderDishesStmt =
              con.prepareStatement(CREATE_ORDER_DISHES_STATEMENT)) {
            List<OrderDish> orderDishes = new ArrayList<>();
            for (OrderDish od : order.getDishes()) {
              createOrderDishesStmt.setInt(1, orderId);
              createOrderDishesStmt.setInt(2, od.getDishId());
              createOrderDishesStmt.setInt(3, od.getQuantity());
              if (od.isLiked() == null) {
                createOrderDishesStmt.setNull(4, java.sql.Types.BOOLEAN);
              } else {
                createOrderDishesStmt.setBoolean(4, od.isLiked());
              }

              try (ResultSet orderDishesRs = createOrderDishesStmt.executeQuery()) {
                if (!orderDishesRs.next()) {
                  throw new SQLException("Failed to create order dish");
                }

                Integer dishId = orderDishesRs.getInt("dish_id");
                Integer quantity = orderDishesRs.getInt("quantity");
                Boolean isLiked = orderDishesRs.getObject("is_liked", Boolean.class);

                orderDishes.add(new OrderDish(dishId, quantity, isLiked));
              }
            }
            o =
                new Order.Builder()
                    .id(orderId)
                    .userId(userId)
                    .promotionId(promotionId)
                    .totalPrice(totalPrice)
                    .status(status)
                    .dishes(orderDishes)
                    .build();
          }
        }
      }
      con.commit();
    } catch (SQLException e) {
      con.rollback();
      throw e;
    }
    outputParam = o;
  }
}
