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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO
    }
}
