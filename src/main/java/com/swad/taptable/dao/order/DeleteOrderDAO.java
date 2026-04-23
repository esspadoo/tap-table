package com.swad.taptable.dao.order;

import com.swad.taptable.dao.AbstractDAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// Done
public class DeleteOrderDAO extends AbstractDAO<Boolean> {
  private static final String STATEMENT = "DELETE FROM orders WHERE id = ?";
  private final int orderId;

  public DeleteOrderDAO(int orderId) {
    this.orderId = orderId;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement preparedStatement = con.prepareStatement(STATEMENT)) {
      preparedStatement.setInt(1, orderId);

      /**
       * Check if the order was successfully deleted. If affectedRows is 1, it means that one row
       * was deleted, which is the expected outcome. If it's 0, it means that no rows were deleted
       */
      int affectedRows = preparedStatement.executeUpdate();
      outputParam = affectedRows == 1;
    } catch (SQLException e) {
      throw e;
    }
  }
}