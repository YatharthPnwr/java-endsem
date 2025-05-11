package com.eventapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Database initializer that runs at application startup to ensure the database
 * schema is created and sample data is loaded.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing database...");
        try {
            initializeDatabase();
            System.out.println("Database initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup code if needed when application is shutting down
    }

    /**
     * Initialize the database by executing the SQL schema script
     */
    private void initializeDatabase() throws SQLException, IOException {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DatabaseUtil.getConnection();
            statement = connection.createStatement();
            
            // Execute the schema creation script
            executeSQLScript(connection, "/database_schema.sql");
            
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Execute an SQL script from a resource file
     */
    private void executeSQLScript(Connection connection, String resourcePath) throws SQLException, IOException {
        // Load the SQL script from resources
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Could not find resource: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder queryBuilder = new StringBuilder();
            String line;
            
            // Read the script line by line
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                queryBuilder.append(line);
                
                // When we encounter a semicolon, execute the statement
                if (line.endsWith(";")) {
                    String query = queryBuilder.toString();
                    try (Statement statement = connection.createStatement()) {
                        System.out.println("Executing SQL: " + query);
                        statement.execute(query);
                    } catch (SQLException e) {
                        // Print error but continue with other statements
                        System.err.println("Error executing SQL: " + query);
                        System.err.println("Error message: " + e.getMessage());
                    }
                    queryBuilder = new StringBuilder();
                }
            }
        }
    }
}