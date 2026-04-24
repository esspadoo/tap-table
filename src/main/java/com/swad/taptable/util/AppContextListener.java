package com.swad.taptable.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Class that listens for servlet context lifecycle events to initialize and clean up
 * application-wide resources. Currently initializes the JWTUtil with the secret key from the
 * context parameter at startup and logs application startup and shutdown events.
 *
 * @author SWAD Team
 */
public class AppContextListener implements ServletContextListener {
  private static final Logger LOGGER =
      LogManager.getLogger(AppContextListener.class, StringFormatterMessageFactory.INSTANCE);

  /**
   * Initializes servlet-context scoped resources when the web application starts.
   *
   * @param sce the servlet context lifecycle event provided by the container.
   */
  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    final String jwtSecret = sce.getServletContext().getInitParameter("jwt.secret");
    JWTUtil.init(jwtSecret);

    LOGGER.info("TapTable application started successfully.");
  }

  /**
   * Logs the shutdown of the web application when the servlet context is destroyed.
   *
   * @param sce the servlet context lifecycle event provided by the container.
   */
  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    LOGGER.info("TapTable application is shutting down.");
  }
}
