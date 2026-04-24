package com.swad.taptable.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

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
