package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.exception.NotValidOrder;
import com.swad.taptable.resources.Order;

import java.sql.PreparedStatement;

public class NewOrderDAO extends AbstractDAO<Order> {
  private final Order order;

  public NewOrderDAO(final Order order) throws NotValidOrder {
    if (order == null)
      throw new NotValidOrder("Order cannot be null");
    if (order.getTotalPrice() < 0)
      throw new NotValidOrder("Price cannot be negative");
    this.order = order;
  }

  @Override
  protected void doAccess() throws Exception {
    final String STATEMENT =
        "INSERT INTO orders(user_id, promotion_id, total_amount, status) " + "VALUES (?, ?, ?, ?)";

    PreparedStatement preparedStatement = con.prepareStatement(STATEMENT);
    preparedStatement.setInt(1, order.getUserId());
    preparedStatement.setInt(2, order.getPromotionId());
    preparedStatement.setFloat(3, order.getTotalPrice());
    preparedStatement.setObject(4, order.getStatus());

    preparedStatement.execute();

  }
}
