package com.eventapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eventapp.util.DatabaseUtil;

/**
 * Data Access Object for handling cart operations in the database.
 */
public class CartDAO {
    
    /**
     * Adds a vendor to the user's cart in the database.
     * 
     * @param userId   The ID of the user
     * @param vendorId The ID of the vendor to add to cart
     * @return true if successfully added, false otherwise
     */
    public boolean addToCart(int userId, int vendorId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            
            // Check if the item is already in the cart
            if (isVendorInCart(connection, userId, vendorId)) {
                return true; // Already in cart, consider it a success
            }
            
            String sql = "INSERT INTO cart_items (user_id, vendor_id) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, vendorId);
            
            int affectedRows = statement.executeUpdate();
            return (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Removes a vendor from the user's cart in the database.
     * 
     * @param userId   The ID of the user
     * @param vendorId The ID of the vendor to remove from cart
     * @return true if successfully removed, false otherwise
     */
    public boolean removeFromCart(int userId, int vendorId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "DELETE FROM cart_items WHERE user_id = ? AND vendor_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, vendorId);
            
            int affectedRows = statement.executeUpdate();
            return (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Clears all items from the user's cart in the database.
     * 
     * @param userId The ID of the user
     * @return true if successfully cleared, false otherwise
     */
    public boolean clearCart(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "DELETE FROM cart_items WHERE user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Gets all vendor IDs in the user's cart from the database.
     * 
     * @param userId The ID of the user
     * @return List of vendor IDs
     */
    public List<Integer> getCartItems(int userId) {
        List<Integer> vendorIds = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT vendor_id FROM cart_items WHERE user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                vendorIds.add(resultSet.getInt("vendor_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return vendorIds;
    }
    
    /**
     * Checks if a vendor is already in the user's cart.
     * 
     * @param connection Already established database connection
     * @param userId     The ID of the user
     * @param vendorId   The ID of the vendor
     * @return true if vendor is in cart, false otherwise
     */
    private boolean isVendorInCart(Connection connection, int userId, int vendorId) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String sql = "SELECT 1 FROM cart_items WHERE user_id = ? AND vendor_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, vendorId);
            resultSet = statement.executeQuery();
            
            return resultSet.next();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }
}
