package com.eventapp.dao;

import com.eventapp.model.AdminVendorView;
import com.eventapp.model.Vendor;
import com.eventapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendorDAO {

    public Vendor findByUserId(int userId) throws SQLException {
        Vendor vendor = null;
        // Assuming your schema uses 'business_name' for vendor's name and 'service_type' for vendor's type
        String sql = "SELECT id, user_id, business_name, description, service_type, base_cost, location, contact_email, contact_phone, rating, image_url, is_active FROM vendors WHERE user_id = ?"; 
        System.out.println("VendorDAO: Fetching vendor by user ID: " + userId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    vendor = mapResultSetToVendor(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorDAO.findByUserId for userId " + userId + ": " + e.getMessage());
            throw e;
        }
        System.out.println("VendorDAO: Vendor " + (vendor != null ? "found" : "not found") + " for user ID: " + userId);
        return vendor;
    }

    public Vendor findById(int vendorId) throws SQLException {
        Vendor vendor = null;
        String sql = "SELECT id, user_id, business_name, description, service_type, base_cost, location, contact_email, contact_phone, rating, image_url, is_active FROM vendors WHERE id = ?";
        System.out.println("VendorDAO: Fetching vendor by ID: " + vendorId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vendorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    vendor = mapResultSetToVendor(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorDAO.findById for vendorId " + vendorId + ": " + e.getMessage());
            throw e;
        }
         System.out.println("VendorDAO: Vendor " + (vendor != null ? "found" : "not found") + " for ID: " + vendorId);
        return vendor;
    }
    
    public List<Vendor> findByType(String type) throws SQLException {
        List<Vendor> vendors = new ArrayList<>();
        // Using 'service_type' as per your schema log
        String sql = "SELECT id, user_id, business_name, description, service_type, base_cost, location, contact_email, contact_phone, rating, image_url, is_active FROM vendors WHERE service_type = ? AND is_active = 1";
        System.out.println("VendorDAO: Fetching vendors by type: " + type);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vendors.add(mapResultSetToVendor(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorDAO.findByType for type " + type + ": " + e.getMessage());
            throw e;
        }
        System.out.println("VendorDAO: Found " + vendors.size() + " vendors for type: " + type);
        return vendors;
    }

    public List<Vendor> findAllActive() throws SQLException {
        List<Vendor> vendors = new ArrayList<>();
        String sql = "SELECT id, user_id, business_name, description, service_type, base_cost, location, contact_email, contact_phone, rating, image_url, is_active FROM vendors WHERE is_active = 1 ORDER BY business_name";
        System.out.println("VendorDAO: Fetching all active vendors.");
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                vendors.add(mapResultSetToVendor(resultSet));
            }
        } catch (SQLException e) {
             System.err.println("SQLException in VendorDAO.findAllActive: " + e.getMessage());
            throw e;
        }
        System.out.println("VendorDAO: Found " + vendors.size() + " active vendors.");
        return vendors;
    }
    
    public List<String> findDistinctVendorTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        // Using 'service_type'
        String sql = "SELECT DISTINCT service_type FROM vendors WHERE is_active = 1 ORDER BY service_type";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                types.add(resultSet.getString("service_type"));
            }
        }
        return types;
    }

    public int add(Vendor vendor) throws SQLException {
        // Using 'business_name' and 'service_type' to match your schema for INSERT
        // contact_email and contact_phone are separate in schema, not combined contact_info
        String sql = "INSERT INTO vendors (user_id, business_name, description, service_type, base_cost, location, contact_email, contact_phone, rating, image_url, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        System.out.println("VendorDAO: Adding new vendor: " + vendor.getName());
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, vendor.getUserId());
            statement.setString(2, vendor.getName()); // Vendor model uses 'name', DB schema uses 'business_name'
            statement.setString(3, vendor.getDescription());
            statement.setString(4, vendor.getType());   // Vendor model uses 'type', DB schema uses 'service_type'
            statement.setDouble(5, vendor.getBaseCost());
            statement.setString(6, vendor.getLocation());
            
            // Splitting contact_info if it's in "email, phone" format; otherwise, adapt
            String contactInfo = vendor.getContactInfo(); // This comes from User.email + User.phone
            String email = "";
            String phone = "";
            if (contactInfo != null && contactInfo.contains(",")) {
                String[] parts = contactInfo.split(",", 2);
                email = parts[0].trim();
                if (parts.length > 1) phone = parts[1].trim();
            } else if (contactInfo != null && contactInfo.contains("@")) {
                email = contactInfo.trim();
            } else if (contactInfo != null) {
                phone = contactInfo.trim(); // Or decide how to handle if it's just one piece of info
            }

            statement.setString(7, email); // Assuming user's email if not directly set
            statement.setString(8, phone); // Assuming user's phone if not directly set
            
            statement.setDouble(9, vendor.getRating());
            statement.setString(10, vendor.getImageUrl());
            statement.setBoolean(11, vendor.isActive()); 

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("VendorDAO.add: Creating vendor failed, no rows affected.");
                throw new SQLException("Creating vendor failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    System.out.println("VendorDAO: Vendor added with ID: " + newId);
                    return newId;
                } else {
                    System.err.println("VendorDAO.add: Creating vendor failed, no ID obtained.");
                    throw new SQLException("Creating vendor failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorDAO.add for vendor " + vendor.getName() + ": " + e.getMessage());
            throw e;
        }
    }

    public boolean update(Vendor vendor) throws SQLException {
        // Using 'business_name' and 'service_type'
        String sql = "UPDATE vendors SET business_name = ?, description = ?, service_type = ?, base_cost = ?, location = ?, contact_email = ?, contact_phone = ?, rating = ?, image_url = ?, is_active = ?, updated_at = NOW() WHERE id = ? AND user_id = ?";
        System.out.println("VendorDAO: Updating vendor ID: " + vendor.getId());
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vendor.getName()); // Maps to business_name
            statement.setString(2, vendor.getDescription());
            statement.setString(3, vendor.getType());   // Maps to service_type
            statement.setDouble(4, vendor.getBaseCost());
            statement.setString(5, vendor.getLocation());

            String contactInfo = vendor.getContactInfo();
            String email = "";
            String phone = "";
            if (contactInfo != null && contactInfo.contains(",")) {
                String[] parts = contactInfo.split(",", 2);
                email = parts[0].trim();
                if (parts.length > 1) phone = parts[1].trim();
            } else if (contactInfo != null && contactInfo.contains("@")) {
                email = contactInfo.trim();
            } else if (contactInfo != null) {
                phone = contactInfo.trim();
            }
            statement.setString(6, email);
            statement.setString(7, phone);

            statement.setDouble(8, vendor.getRating());
            statement.setString(9, vendor.getImageUrl());
            statement.setBoolean(10, vendor.isActive());
            statement.setInt(11, vendor.getId());
            statement.setInt(12, vendor.getUserId());

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;
            System.out.println("VendorDAO: Update vendor ID " + vendor.getId() + (success ? " successful." : " failed."));
            return success;
        } catch (SQLException e) {
            System.err.println("SQLException in VendorDAO.update for vendorId " + vendor.getId() + ": " + e.getMessage());
            throw e;
        }
    }
    
    public boolean updateVendorStatus(int vendorId, boolean isActive) throws SQLException {
        String sql = "UPDATE vendors SET is_active = ?, updated_at = NOW() WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isActive);
            statement.setInt(2, vendorId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<AdminVendorView> findAllForAdminView() throws SQLException {
        List<AdminVendorView> views = new ArrayList<>();
        String sql = "SELECT v.id as vendor_id, v.business_name as vendor_name, v.service_type, v.is_active, v.created_at, " +
                     "u.id as user_id, u.name as user_name, u.email as user_email " +
                     "FROM vendors v JOIN users u ON v.user_id = u.id ORDER BY v.created_at DESC"; // Removed is_deleted
        System.out.println("VendorDAO: Fetching all vendors for admin view.");
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                AdminVendorView view = new AdminVendorView();
                view.setVendorId(resultSet.getInt("vendor_id"));
                view.setVendorName(resultSet.getString("vendor_name"));
                view.setVendorType(resultSet.getString("service_type")); // Using service_type from DB
                view.setActive(resultSet.getBoolean("is_active"));
                view.setRegistrationDate(resultSet.getTimestamp("created_at"));
                view.setUserId(resultSet.getInt("user_id"));
                view.setUserName(resultSet.getString("user_name"));
                view.setUserEmail(resultSet.getString("user_email"));
                views.add(view);
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorDAO.findAllForAdminView: " + e.getMessage());
            throw e;
        }
        System.out.println("VendorDAO: Found " + views.size() + " vendors for admin view.");
        return views;
    }

    private Vendor mapResultSetToVendor(ResultSet rs) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setId(rs.getInt("id"));
        vendor.setUserId(rs.getInt("user_id"));
        vendor.setName(rs.getString("business_name")); // Map business_name from DB to Vendor's name
        vendor.setType(rs.getString("service_type"));   // Map service_type from DB to Vendor's type
        vendor.setDescription(rs.getString("description"));
        // Construct contactInfo for the model, or handle separately if model changes
        String email = rs.getString("contact_email");
        String phone = rs.getString("contact_phone");
        vendor.setContactInfo(email + (phone != null && !phone.isEmpty() ? ", " + phone : ""));
        vendor.setLocation(rs.getString("location"));
        vendor.setBaseCost(rs.getDouble("base_cost"));
        vendor.setImageUrl(rs.getString("image_url"));
        vendor.setActive(rs.getBoolean("is_active"));
        vendor.setRating(rs.getDouble("rating"));
        return vendor;
    }
}