/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class ChangeUserRoleDAO extends AbstractDAO<User> {

  private static final String STATEMENT =
      "UPDATE users SET role = ?::USER_ROLE WHERE id = ? AND role != 'ADMIN'"
          + " RETURNING id, username, email, name, surname, phone_number, role";

  private final int targetUserId;
  private final UserRole newRole;

  public ChangeUserRoleDAO(final int targetUserId, final UserRole newRole) {
    this.targetUserId = targetUserId;
    this.newRole = newRole;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setString(1, newRole.name());
      ps.setInt(2, targetUserId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          outputParam =
              new User.Builder()
                  .id(rs.getInt("id"))
                  .username(rs.getString("username"))
                  .email(rs.getString("email"))
                  .name(rs.getString("name"))
                  .surname(rs.getString("surname"))
                  .phoneNumber(rs.getString("phone_number"))
                  .role(UserRole.valueOf(rs.getString("role")))
                  .build();
        }
      }
    }
  }
}
