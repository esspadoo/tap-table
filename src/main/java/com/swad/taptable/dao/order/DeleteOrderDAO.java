package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteOrderDAO extends AbstractDAO<Boolean> {
  private final int orderId;

  public DeleteOrderDAO(int orderId) {
    this.orderId = orderId;
  }

  @Override
  protected void doAccess() throws Exception {
    final String STATEMENT = "DELETE FROM orders WHERE id = ?";

    try (PreparedStatement preparedStatement = con.prepareStatement(STATEMENT)) {
      preparedStatement.setInt(1, orderId);
      preparedStatement.executeUpdate();
      outputParam = true;
    } catch (SQLException ex) {
      outputParam = false;
      throw new SQLException("can't delete order with id " + orderId);
    }
  }
}
