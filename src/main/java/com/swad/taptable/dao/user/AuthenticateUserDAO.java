package com.swad.taptable.dao.user;

import org.mindrot.jbcrypt.BCrypt;
import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.User;
import com.swad.taptable.resources.UserRole;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Verifies user credentials and returns the user object on success.
 *
 * <p>
 * Returns {@code null} if the email is not found or the password does not match.
 */
public class AuthenticateUserDAO extends AbstractDAO<User> {

  private static final String STATEMENT = "SELECT * FROM users WHERE email = ?";

  private final String email;
  private final String password;

  public AuthenticateUserDAO(final String email, final String password) {
    this.email = email;
    this.password = password;
  }

  @Override
  protected void doAccess() throws Exception {
    User user = null;
    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setString(1, email);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          final int userId = rs.getInt("id");
          final String email = rs.getString("email");
          final String name = rs.getString("name");
          final String surname = rs.getString("surname");
          final String phoneNumber = rs.getString("phone_number");
          final String passwordHash = rs.getString("password_hash");
          final UserRole role = UserRole.valueOf(rs.getString("role"));

          if (BCrypt.checkpw(password, passwordHash)) {
            user = new User.Builder().id(userId).email(email).name(name).surname(surname)
                    .phoneNumber(phoneNumber).role(role).passwordHash(passwordHash).build();
          }
        }
      }
    }

    outputParam = user;
  }
}