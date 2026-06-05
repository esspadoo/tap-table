/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO that deletes a non-admin user account from the database.
 *
 * @author SWAD Team
 */
public final class AdminDeleteUserDAO extends AbstractDAO<Boolean> {

  private static final String STATEMENT =
      "DELETE FROM users WHERE id = ? AND role != 'ADMIN' RETURNING id";

  private final int targetUserId;

  /**
   * Creates a new DAO object.
   *
   * @param targetUserId the identifier of the user to delete.
   */
  public AdminDeleteUserDAO(final int targetUserId) {
    this.targetUserId = targetUserId;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setInt(1, targetUserId);
      try (ResultSet rs = ps.executeQuery()) {
        outputParam = rs.next();
      }
    }
  }
}
