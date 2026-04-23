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

public class EditOrderStatusDAO extends AbstractDAO<Order> {
    private static final String ORDER_UPDATE_STATEMENT =
            "UPDATE orders SET status = ? WHERE id = ? RETURNING *";
    private static final String ORDER_DISHES_STATEMENT =
            "SELECT dish_id, quantity, is_liked FROM order_dishes WHERE order_id = ?";
    private final Integer orderId;
    private final OrderStatus orderStatus;

    public EditOrderStatusDAO(final Integer orderId, final OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    @Override
    protected void doAccess() throws SQLException {
        Order o = null;

        con.setAutoCommit(false);

        try (PreparedStatement orderPstmt = con.prepareStatement(ORDER_UPDATE_STATEMENT)) {
            orderPstmt.setString(1, orderStatus.name());
            orderPstmt.setInt(2, orderId);

            try (ResultSet orderRs = orderPstmt.executeQuery()) {
                if (!orderRs.next()) {
                    // Early return if the order with the provided ID doesn't exist
                    return;
                }

                Integer orderId = orderRs.getInt("id");
                Integer userId = orderRs.getInt("user_id");
                Integer promotionId = orderRs.getInt("promotion_id");
                Float totalPrice = orderRs.getFloat("total_amount");

                List<OrderDish> orderDishes = new ArrayList<>();
                try (PreparedStatement orderDishesPstmt = con.prepareStatement(ORDER_DISHES_STATEMENT)) {
                    orderDishesPstmt.setInt(1, orderId);
                    try (ResultSet orderDishesRs = orderDishesPstmt.executeQuery()) {
                        while (orderDishesRs.next()) {
                            Integer dishId = orderDishesRs.getInt("dish_id");
                            Integer quantity = orderDishesRs.getInt("quantity");
                            Boolean isLiked = orderDishesRs.getBoolean("is_liked");

                            orderDishes.add(new OrderDish(dishId, quantity, isLiked));
                        }
                    }
                }

                o = new Order.Builder().id(orderId).userId(userId).promotionId(promotionId)
                        .totalPrice(totalPrice).status(orderStatus).dishes(orderDishes).build();
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        }

        outputParam = o;
    }
}