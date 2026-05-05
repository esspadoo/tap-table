/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderDish;
import com.swad.taptable.resources.OrderStatus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetOrderDAO extends AbstractDAO<Order> {
  private static final String ORDER_STATEMENT = "SELECT * FROM orders WHERE id = ?";
  private static final String ORDER_DISHES_STATEMENT =
      "SELECT dish_id, quantity, is_liked FROM order_dishes WHERE order_id = ?";

  private final int orderId;

  public GetOrderDAO(int orderId) {
    this.orderId = orderId;
  }

  @Override
  protected void doAccess() throws Exception {
    Order o = null;

    try (PreparedStatement orderPstmt = con.prepareStatement(ORDER_STATEMENT)) {
      orderPstmt.setInt(1, orderId);

      try (ResultSet rs = orderPstmt.executeQuery()) {
        if (!rs.next()) return;

        int userId = rs.getInt("user_id");
        Integer promotionId = rs.getObject("promotion_id", Integer.class);
        float totalPrice = rs.getFloat("total_amount");
        OrderStatus status = OrderStatus.valueOf(rs.getString("status"));

        List<OrderDish> orderDishes = new ArrayList<>();
        try (PreparedStatement dishesPstmt = con.prepareStatement(ORDER_DISHES_STATEMENT)) {
          dishesPstmt.setInt(1, orderId);

          try (ResultSet dishesRs = dishesPstmt.executeQuery()) {
            while (dishesRs.next()) {
              int dishId = dishesRs.getInt("dish_id");
              int quantity = dishesRs.getInt("quantity");
              boolean isLiked = dishesRs.getBoolean("is_liked");

              orderDishes.add(new OrderDish(dishId, quantity, isLiked));
            }
          }
        }

        o =
            new Order.Builder()
                .id(orderId)
                .status(status)
                .totalPrice(totalPrice)
                .userId(userId)
                .promotionId(promotionId)
                .dishes(orderDishes)
                .build();
      }
    }

    outputParam = o;
  }
}
