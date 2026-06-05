/* Copyright (c) 2026 University of Padua, Italy - MIT License */
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

/**
 * DAO that retrieves all orders together with their dish line items from the database.
 *
 * @author SWAD Team
 */
public class GetAllOrdersDAO extends AbstractDAO<ResourceList<Order>> {

  private static final String STATEMENT =
      "SELECT o.id, o.user_id, o.promotion_id, o.total_amount, o.status,"
          + " od.dish_id, od.quantity, od.is_liked, d.name AS dish_name"
          + " FROM orders o"
          + " LEFT JOIN order_dishes od ON od.order_id = o.id"
          + " LEFT JOIN dishes d ON d.id = od.dish_id"
          + " ORDER BY o.id";

  @Override
  protected void doAccess() throws Exception {
    List<Order> orders = new ArrayList<>();
    Order.Builder current = null;
    List<OrderDish> currentDishes = null;
    int currentId = -1;

    try (PreparedStatement ps = con.prepareStatement(STATEMENT);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        int orderId = rs.getInt("id");

        if (orderId != currentId) {
          if (current != null) {
            orders.add(current.dishes(currentDishes).build());
          }
          currentId = orderId;
          currentDishes = new ArrayList<>();
          current =
              new Order.Builder()
                  .id(orderId)
                  .userId(rs.getInt("user_id"))
                  .promotionId(rs.getObject("promotion_id", Integer.class))
                  .totalPrice(rs.getFloat("total_amount"))
                  .status(OrderStatus.valueOf(rs.getString("status")));
        }

        int dishId = rs.getInt("dish_id");
        // FIXME: check that wasNull
        if (!rs.wasNull()) {
          currentDishes.add(
              new OrderDish(
                  dishId,
                  rs.getInt("quantity"),
                  rs.getObject("is_liked", Boolean.class),
                  rs.getString("dish_name")));
        }
      }
    }

    if (current != null) {
      orders.add(current.dishes(currentDishes).build());
    }

    outputParam = new ResourceList<>(orders);
  }
}
