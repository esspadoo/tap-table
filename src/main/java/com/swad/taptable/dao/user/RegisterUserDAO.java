package com.swad.taptable.dao.user;

import com.swad.taptable.dao.AbstractDAO;
import com.swad.taptable.resources.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO for the registration of a new user. Inserts a new row into the users table and returns the
 * generated user ID.
 *
 * @author SWAD Team
 */
public class RegisterUserDAO extends AbstractDAO<Integer> {

  private static final String STATEMENT =
          "INSERT INTO users (username, email, name, surname, phone_number, role, password_hash) "
                  + "VALUES (?, ?, ?, ?, ?, 'CUSTOMER'::USER_ROLE, ?) RETURNING id";

  private final User user;

  public RegisterUserDAO(final User user) {
    this.user = user;
  }

  @Override
  protected void doAccess() throws Exception {
    try (PreparedStatement ps = con.prepareStatement(STATEMENT)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getEmail());
      ps.setString(3, user.getName());
      ps.setString(4, user.getSurname());
      ps.setString(5, user.getPhoneNumber());
      ps.setString(6, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          outputParam = rs.getInt("id");
          LOGGER.debug("User inserted successfully with id %d.", outputParam);
        }
      }
    }
  }
}