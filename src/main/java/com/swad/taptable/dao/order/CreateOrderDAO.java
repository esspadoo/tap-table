package com.swad.taptable.dao.order;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderDish;
import com.swad.taptable.resources.OrderStatus;

public class CreateOrderDAO extends AbstractDAO<Order> {
    private static final String CREATE_ORDER_STATEMENT =
            "INSERT INTO orders (user_id, promotion_id, total_amount, status) VALUES (?, ?, ?, ?) RETURNING *";
    private static final String CREATE_ORDER_DISHES_STATEMENT =
            "INSERT INTO order_dishes (order_id, dish_id, quantity, is_liked) VALUES (?, ?, ?, ?) RETURNING *";

    private final Order order;

    public CreateOrderDAO(final Order order) {
        this.order = order;
    }

    @Override
    protected void doAccess() throws Exception {
        Order o = null;

        con.setAutoCommit(false);

        try (PreparedStatement createOrderStmt = con.prepareStatement(CREATE_ORDER_STATEMENT)) {
            createOrderStmt.setInt(1, order.getUserId());
            createOrderStmt.setInt(2, order.getPromotionId());
            createOrderStmt.setFloat(3, order.getTotalPrice());
            createOrderStmt.setString(4, order.getStatus().name());

            try (ResultSet orderRs = createOrderStmt.executeQuery()) {
                if (!orderRs.next()) {
                    throw new SQLException("Failed to create order");
                }

                Integer orderId = orderRs.getInt("id");
                Integer userId = orderRs.getInt("user_id");
                Integer promotionId = orderRs.getInt("promotion_id");
                Float totalPrice = orderRs.getFloat("total_amount");
                OrderStatus status = OrderStatus.valueOf(orderRs.getString("status"));

                List<OrderDish> orderDishes = new ArrayList<>();
                for (OrderDish od : order.getDishes()) {
                    try (PreparedStatement createOrderDishesStmt =
                                 con.prepareStatement(CREATE_ORDER_DISHES_STATEMENT)) {
                        createOrderDishesStmt.setInt(1, orderId);
                        createOrderDishesStmt.setInt(2, od.getDishId());
                        createOrderDishesStmt.setInt(3, od.getQuantity());
                        createOrderDishesStmt.setBoolean(4, od.isLiked());

                        try (ResultSet orderDishesRs = createOrderDishesStmt.executeQuery()) {
                            if (!orderDishesRs.next()) {
                                throw new SQLException("Failed to create order dish");
                            }

                            Integer dishId = orderDishesRs.getInt("dish_id");
                            Integer quantity = orderDishesRs.getInt("quantity");
                            Boolean isLiked = orderDishesRs.getBoolean("is_liked");

                            orderDishes.add(new OrderDish(dishId, quantity, isLiked));
                        }
                    }
                }

                o = new Order.Builder().id(orderId).userId(userId).promotionId(promotionId)
                        .totalPrice(totalPrice).status(status).dishes(orderDishes).build();
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        }

        outputParam = o;
    }

}