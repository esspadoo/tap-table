package com.swad.taptable.dao;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Verifies user credentials and returns the user id on success.
 *
 * <p>
 * Returns {@code null} if the email is not found or the password does not match.
 */
public class AuthenticateUserDAO extends AbstractDAO<Integer> {

  private final String email;
  private final String password;

  public AuthenticateUserDAO(final String email, final String password) {
    this.email = email;
    this.password = password;
  }

  /**
   * Checks the provided email and password against the database. If the email is found and the
   * password matches the stored hash, the associated user id is returned via {@code outputParam}.
   * Otherwise, {@code outputParam} remains {@code null
   * 
   * @throws Exception if any database error occurs.
   */
  @Override
  protected void doAccess() throws Exception {
    final String sql = "SELECT id, password_hash FROM users WHERE email = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, email);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          // email not found - outputParam stays null
          return;
        }

        int userId = rs.getInt("id");
        String hash = rs.getString("password_hash");

        if (BCrypt.checkpw(password, hash)) {
          outputParam = userId;
        }
      }
    }
  }
}
