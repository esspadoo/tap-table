package com.swad.taptable.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * @author SWAD Team
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO
        // Here we can initialize the connection pool and any other resources we need at app startup
        // This class allows us to retrieve context parameters defined in web.xml, which is useful
        // (e.g. for DB connection config)
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO
    }
}
