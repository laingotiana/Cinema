package com.cinema.listener;

import com.cinema.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener to properly shut down resources when the web application is stopped.
 */
@WebListener
public class DatabaseConnectionListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to do on startup; DatabaseConnection initializes lazily via its static block
        logger.info("ServletContext initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("ServletContext is being destroyed - closing DataSource if necessary");
        try {
            DatabaseConnection.closeDataSource();
        } catch (Exception e) {
            logger.warn("Erreur lors de la fermeture du pool de connexions", e);
        }
    }
}
