/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.ResourceList;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class GetAllUsersDAO extends AbstractDAO<ResourceList<User>> {

  private static final String STATEMENT =
      "SELECT id, username, email, name, surname, phone_number, role"
          + " FROM users ORDER BY id ASC";

  @Override
  protected void doAccess() throws Exception {
    List<User> users = new ArrayList<>();

    try (PreparedStatement ps = con.prepareStatement(STATEMENT);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        users.add(
            new User.Builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .surname(rs.getString("surname"))
                .phoneNumber(rs.getString("phone_number"))
                .role(UserRole.valueOf(rs.getString("role")))
                .build());
      }
    }

    outputParam = new ResourceList<>(users);
  }
}
