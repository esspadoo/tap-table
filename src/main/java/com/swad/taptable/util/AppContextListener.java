package com.swad.taptable.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
// import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Initializes the application context, including the JWT secret key. This listener is registered in
 * web.xml and runs when the application starts.
 *
 * @author SWAD Team
 */
public class AppContextListener implements ServletContextListener {

  private static final Logger LOGGER =
      LogManager.getLogger(AppContextListener.class, StringFormatterMessageFactory.INSTANCE);

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    final String jwtSecret = sce.getServletContext().getInitParameter("jwt.secret");
    JWTUtil.init(jwtSecret);

    LOGGER.info("TapTable application started successfully.");
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    LOGGER.info("TapTable application is shutting down.");
  }
}
