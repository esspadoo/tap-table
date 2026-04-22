package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.Order;

import java.sql.PreparedStatement;

public class EditOrderDAO extends AbstractDAO<Order> {
    private final Order order;

    public EditOrderDAO(Order order) {
        this.order = order;
    }

    @Override
    protected void doAccess() throws Exception {
        final String STATEMENT = "UPDATE orders " +
                "SET status = ? " +
                "WHERE id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(STATEMENT)){
            preparedStatement.setObject(1, order.getStatus());
            preparedStatement.setInt(2, order.getId());
            preparedStatement.executeUpdate();
        }

    }
}