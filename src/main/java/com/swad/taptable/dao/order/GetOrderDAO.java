package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;
import com.swad.taptable.resources.OrderStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetOrderDAO extends AbstractDAO<Order> {
    private final int orderId;

    public GetOrderDAO(int orderId) {
        this.orderId = orderId;
    }

    @Override
    protected void doAccess() throws Exception {
        final String STATEMENT = "SELECT * FROM orders " +
                "WHERE id = ?";
        List<Order> orders = new ArrayList<>();
        PreparedStatement preparedStatement = con.prepareStatement(STATEMENT);
        preparedStatement.setInt(1, orderId);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(new Order(resultSet.getInt("id"), (OrderStatus) resultSet.getObject("status"),
                        resultSet.getFloat("total_amount"), resultSet.getInt("user_id"),
                        resultSet.getInt("promotion_id")));
            }
        }catch (SQLException e){
            throw new SQLException("Order not found.");
        }finally {
            preparedStatement.close();
        }

        outputParam = orders.getFirst();

    }
}