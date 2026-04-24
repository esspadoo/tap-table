package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class EditUserDAO extends AbstractDAO<User> {

  private static final String STATEMENT =
      "UPDATE users SET name = ?, surname = ?, email = ?, phone_number = ? WHERE id = ? RETURNING id, username, email, name, surname, phone_number,role";

  private final Integer id;
  private final User user;

  public EditUserDAO(final Integer id, final User user) {
    this.id = id;
    this.user = user;
  }

  @Override
  protected void doAccess() throws Exception {
    User u = null;

    try (PreparedStatement stmt = con.prepareStatement(STATEMENT)) {
      stmt.setString(1, user.getName());
      stmt.setString(2, user.getSurname());
      stmt.setString(3, user.getEmail());
      stmt.setString(4, user.getPhoneNumber());
      stmt.setInt(5, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          u = new User.Builder().id(rs.getInt("id")).username(rs.getString("username"))
              .email(rs.getString("email")).name(rs.getString("name"))
              .surname(rs.getString("surname")).phoneNumber(rs.getString("phone_number"))
              .role(UserRole.valueOf(rs.getString("role"))).build();
        }
      }
    }

    outputParam = u;
  }
}
