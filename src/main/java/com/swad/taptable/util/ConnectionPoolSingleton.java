/* Copyright (c) 2026 University of Padua, Italy - MIT License */
package com.swad.taptable.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Acquires and exposes the container-managed DataSource via JNDI.
 *
 * <p>Connection pool lifecycle (sizing, timeouts, credentials) is fully owned by Tomcat -
 * configured in {@code context.xml} outside the WAR. This class only performs the lookup and
 * delegates every call to the container.
 *
 * @author SWAD Team
 */
public final class ConnectionPoolSingleton {

  private static final Logger LOGGER =
      LogManager.getLogger(ConnectionPoolSingleton.class, StringFormatterMessageFactory.INSTANCE);

  private static final String JNDI_NAME = "java:comp/env/jdbc/taptable";

  private static final DataSource ds;

  static {
    try {
      ds = (DataSource) new InitialContext().lookup(JNDI_NAME);
      LOGGER.info("Connection pool to the database successfully acquired.");
    } catch (NamingException e) {
      LOGGER.fatal("Unable to acquire the connection pool to the database.", e);
      throw new ExceptionInInitializerError(
          e); // https://www.baeldung.com/java-exceptionininitializererror#the-exception
    }
  }

  /**
   * Private constructor to prevent instantiation of this utility class. This class is not meant to
   * be instantiated since it provides a static method for acquiring connections from the pool, and
   * all the state is managed by the container. If an attempt is made to instantiate this class, an
   * AssertionError is thrown to indicate that this is not allowed.
   */
  private ConnectionPoolSingleton() {
    throw new AssertionError(
        String.format("No instances of %s allowed.", ConnectionPoolSingleton.class.getName()));
  }

  /**
   * Returns a connection from the container-managed pool.
   *
   * @return a {@link Connection} from the pool
   * @throws SQLException if no connection is available
   */
  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }
}
