package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderDish;
import com.swad.taptable.resources.OrderStatus;
import com.swad.taptable.resources.ResourceList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetAllOrdersDAO extends AbstractDAO<ResourceList<Order>> {

  private static final String ORDERS_STATEMENT = "SELECT * FROM orders";
  private static final String ORDER_DISHES_STATEMENT =
      "SELECT dish_id, quantity, is_liked  FROM order_dishes WHERE order_id = ?";

  @Override
  protected void doAccess() throws Exception {
    List<Order> orders = new ArrayList<>();

    try (PreparedStatement ordersPstmt = con.prepareStatement(ORDERS_STATEMENT);
        ResultSet ordersRs = ordersPstmt.executeQuery()) {
      while (ordersRs.next()) {
        int orderId = ordersRs.getInt("id");
        int userId = ordersRs.getInt("user_id");
        Integer promotionId = ordersRs.getObject("promotion_id", Integer.class);
        float totalPrice = ordersRs.getFloat("total_amount");
        OrderStatus status = OrderStatus.valueOf(ordersRs.getString("status"));

        try (PreparedStatement dishesPstmt = con.prepareStatement(ORDER_DISHES_STATEMENT)) {
          dishesPstmt.setInt(1, orderId);

          try (ResultSet dishesRs = dishesPstmt.executeQuery()) {
            List<OrderDish> orderDishes = new ArrayList<>();
            while (dishesRs.next()) {
              int dishId = dishesRs.getInt("dish_id");
              int quantity = dishesRs.getInt("quantity");
              boolean isLiked = dishesRs.getBoolean("is_liked");

              orderDishes.add(new OrderDish(dishId, quantity, isLiked));
            }

            orders.add(new Order.Builder().id(orderId).userId(userId).promotionId(promotionId)
                .totalPrice(totalPrice).status(status).dishes(orderDishes).build());
          }
        }
      }
    }

    outputParam = new ResourceList<>(orders);
  }
}
