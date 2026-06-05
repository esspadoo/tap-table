/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;

public final class ChangePasswordDAO extends AbstractDAO<Boolean> {

  private static final String SELECT_HASH = "SELECT password_hash FROM users WHERE id = ?";
  private static final String UPDATE_HASH = "UPDATE users SET password_hash = ? WHERE id = ?";

  private final int userId;
  private final String currentPassword;
  private final String newPassword;

  public ChangePasswordDAO(
      final int userId, final String currentPassword, final String newPassword) {
    this.userId = userId;
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  @Override
  protected void doAccess() throws Exception {
    String storedHash = null;

    try (PreparedStatement ps = con.prepareStatement(SELECT_HASH)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          storedHash = rs.getString("password_hash");
        }
      }
    }

    if (storedHash == null || !BCrypt.checkpw(currentPassword, storedHash)) {
      outputParam = false;
      return;
    }

    try (PreparedStatement ps = con.prepareStatement(UPDATE_HASH)) {
      ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
      ps.setInt(2, userId);
      ps.executeUpdate();
    }

    outputParam = true;
  }
}
