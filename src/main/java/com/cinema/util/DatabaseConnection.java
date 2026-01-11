package com.cinema.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestionnaire de connexions à la base de données PostgreSQL
 * Utilise HikariCP pour le pool de connexions
 */
public class DatabaseConnection {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static HikariDataSource dataSource;
    
    static {
        try {
            initDataSource();
        } catch (IOException e) {
            logger.error("Erreur d'initialisation de la base de données", e);
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }
    
    /**
     * Initialise le pool de connexions HikariCP
     */
    private static void initDataSource() throws IOException {
        Properties props = loadProperties();
        
        HikariConfig config = new HikariConfig();
        
        // Configuration de base
        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "5432");
        String dbName = props.getProperty("db.name", "cinema_db");
        String username = props.getProperty("db.username", "cinema_user");
        String password = props.getProperty("db.password");
        
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName));
        config.setUsername(username);
        config.setPassword(password);
    // Explicit driver class to ensure the JDBC driver is loaded in servlet containers
    config.setDriverClassName("org.postgresql.Driver");
        
        // Configuration du pool
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimumIdle", "5")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000")));
        
        // Propriétés PostgreSQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        // Pool name pour le monitoring
        config.setPoolName("CinemaHikariPool");
        
        dataSource = new HikariDataSource(config);
        logger.info("Pool de connexions HikariCP initialisé avec succès");
    }
    
    /**
     * Charge les propriétés de connexion depuis db.properties
     */
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            
            if (input == null) {
                logger.warn("db.properties introuvable, utilisation des valeurs par défaut");
                return props;
            }
            
            props.load(input);
            logger.info("Configuration de base de données chargée depuis db.properties");
        }
        
        return props;
    }
    
    /**
     * Obtient une connexion depuis le pool
     * 
     * @return Connexion active
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Le pool de connexions n'est pas initialisé");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Ferme le pool de connexions (à appeler lors de l'arrêt de l'application)
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de connexions fermé");
        }
    }
    
    /**
     * Obtient des statistiques sur le pool
     * 
     * @return Statistiques formatées
     */
    public static String getPoolStats() {
        if (dataSource == null) {
            return "Pool non initialisé";
        }
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Waiting: %d, Total: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
            dataSource.getHikariPoolMXBean().getTotalConnections()
        );
    }
}
