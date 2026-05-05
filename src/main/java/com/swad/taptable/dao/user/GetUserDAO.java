/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class GetUserDAO extends AbstractDAO<User> {

  private static final String STATEMENT =
      "SELECT id, username, email, name, surname, phone_number, role FROM users WHERE id = ?";

  private final Integer id;

  public GetUserDAO(final Integer id) {
    this.id = id;
  }

  @Override
  protected void doAccess() throws Exception {
    User u = null;

    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          u =
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

    outputParam = u;
  }
}
