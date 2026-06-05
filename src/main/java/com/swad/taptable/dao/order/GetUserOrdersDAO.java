/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderDish;
import com.swad.taptable.resources.OrderStatus;
import com.swad.taptable.resources.ResourceList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetUserOrdersDAO extends AbstractDAO<ResourceList<Order>> {

  private static final String ORDERS_STATEMENT = "SELECT * FROM orders WHERE user_id = ?";
  private static final String ORDER_DISHES_STATEMENT =
      "SELECT dish_id, quantity, is_liked FROM order_dishes WHERE order_id = ?";

  private final int userId;

  public GetUserOrdersDAO(int userId) {
    this.userId = userId;
  }

  @Override
  protected void doAccess() throws SQLException {
    List<Order> orders = new ArrayList<>();

    try (PreparedStatement ordersPstmt = con.prepareStatement(ORDERS_STATEMENT)) {
      ordersPstmt.setInt(1, userId);

      try (ResultSet ordersRs = ordersPstmt.executeQuery()) {
        while (ordersRs.next()) {
          Integer orderId = ordersRs.getInt("id");
          Integer promotionId = ordersRs.getObject("promotion_id", Integer.class);
          float totalPrice = ordersRs.getFloat("total_amount");
          OrderStatus status = OrderStatus.valueOf(ordersRs.getString("status"));

          List<OrderDish> orderDishes = new ArrayList<>();
          try (PreparedStatement dishesPstmt = con.prepareStatement(ORDER_DISHES_STATEMENT)) {
            dishesPstmt.setInt(1, orderId);

            try (ResultSet dishesRs = dishesPstmt.executeQuery()) {
              while (dishesRs.next()) {
                int dishId = dishesRs.getInt("dish_id");
                int quantity = dishesRs.getInt("quantity");
                Boolean isLiked = dishesRs.getObject("is_liked", Boolean.class);

                orderDishes.add(new OrderDish(dishId, quantity, isLiked));
              }
            }

            orders.add(
                new Order.Builder()
                    .id(orderId)
                    .userId(userId)
                    .promotionId(promotionId)
                    .totalPrice(totalPrice)
                    .status(status)
                    .dishes(orderDishes)
                    .build());
          }
        }
      }
    }

    outputParam = new ResourceList<>(orders);
  }
}
