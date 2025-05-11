package com.eventapp.dao;

import com.eventapp.model.User;
import com.eventapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User-related database operations.
 */
public class UserDAO {
    
    /**
     * Find user by email and password for authentication.
     * 
     * @param email User's email
     * @param password User's password
     * @return User object if authentication successful, null otherwise
     */
    public User findByEmailAndPassword(String email, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password); // As per requirements, not hashing passwords
            
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        return null;
    }
    
    /**
     * Check if an email is already in use.
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT id FROM users WHERE email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            
            resultSet = statement.executeQuery();
            return resultSet.next(); // True if email exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }
    
    /**
     * Register a new user in the database.
     * 
     * @param user User object with registration data
     * @return The ID of the newly registered user, or -1 if registration failed
     */
    public int registerUser(User user) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "INSERT INTO users (name, email, phone, password, user_type) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getPassword()); // As per requirements, not hashing passwords
            statement.setString(5, user.getUserType());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    /**
     * Find a user by their ID.
     * 
     * @param userId User ID
     * @return User object if found, null otherwise
     */
    public User findById(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM users WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(resultSet, statement, connection);
        }
        return null;
    }
    
    /**
     * Update a user's profile information in the database.
     * 
     * @param user User object with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setInt(4, user.getId());
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, statement, connection);
        }
    }
    
    /**
     * Update a user's password in the database.
     * 
     * @param userId User ID
     * @param newPassword New password
     * @return true if password was updated successfully, false otherwise
     */
    public boolean updatePassword(int userId, String newPassword) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            
            statement.setString(1, newPassword); // As per requirements, not hashing passwords
            statement.setInt(2, userId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, statement, connection);
        }
    }
    
    /**
     * Map a database result set to a User object.
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setUserType(resultSet.getString("user_type"));
        // Not setting password for security reasons
        return user;
    }
    
    /**
     * Close database resources.
     */
    private void closeResources(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}