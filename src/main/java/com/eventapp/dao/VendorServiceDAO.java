package com.eventapp.dao;

import com.eventapp.model.VendorService;
import com.eventapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendorServiceDAO {

    public List<VendorService> getServicesByVendorId(int vendorId) throws SQLException {
        List<VendorService> services = new ArrayList<>();
        String sql = "SELECT * FROM vendor_services WHERE vendor_id = ? ORDER BY name"; // Removed is_deleted
        System.out.println("VendorServiceDAO: Fetching services for vendor ID: " + vendorId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vendorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    services.add(mapResultSetToVendorService(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.getServicesByVendorId for vendorId " + vendorId + ": " + e.getMessage());
            throw e;
        }
        System.out.println("VendorServiceDAO: Found " + services.size() + " services for vendor ID: " + vendorId);
        return services;
    }

    public VendorService getServiceById(int serviceId) throws SQLException {
        VendorService service = null;
        String sql = "SELECT * FROM vendor_services WHERE id = ?"; // Removed is_deleted
        System.out.println("VendorServiceDAO: Fetching service by ID: " + serviceId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    service = mapResultSetToVendorService(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.getServiceById for serviceId " + serviceId + ": " + e.getMessage());
            throw e;
        }
        System.out.println("VendorServiceDAO: Service " + (service != null ? "found" : "not found") + " for ID: " + serviceId);
        return service;
    }

    public boolean addService(VendorService service) throws SQLException {
        String sql = "INSERT INTO vendor_services (vendor_id, name, description, service_type, price, location, image_url, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        System.out.println("VendorServiceDAO: Adding new service: " + service.getName() + " for vendor ID: " + service.getVendorId());
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, service.getVendorId());
            statement.setString(2, service.getName());
            statement.setString(3, service.getDescription());
            statement.setString(4, service.getServiceType());
            statement.setDouble(5, service.getPrice());
            statement.setString(6, service.getLocation());
            statement.setString(7, service.getImageUrl());
            statement.setBoolean(8, service.isActive());

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;
            System.out.println("VendorServiceDAO: Add service " + (success ? "successful." : "failed."));
            return success;
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.addService for service " + service.getName() + ": " + e.getMessage());
            throw e;
        }
    }

    public boolean updateService(VendorService service) throws SQLException {
        String sql = "UPDATE vendor_services SET name = ?, description = ?, service_type = ?, price = ?, location = ?, image_url = ?, is_active = ?, updated_at = NOW() WHERE id = ? AND vendor_id = ?";
        System.out.println("VendorServiceDAO: Updating service ID: " + service.getId());
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, service.getName());
            statement.setString(2, service.getDescription());
            statement.setString(3, service.getServiceType());
            statement.setDouble(4, service.getPrice());
            statement.setString(5, service.getLocation());
            statement.setString(6, service.getImageUrl());
            statement.setBoolean(7, service.isActive());
            statement.setInt(8, service.getId());
            statement.setInt(9, service.getVendorId());

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;
            System.out.println("VendorServiceDAO: Update service ID " + service.getId() + (success ? " successful." : " failed."));
            return success;
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.updateService for serviceId " + service.getId() + ": " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteService(int serviceId) throws SQLException {
        // Hard delete since we removed is_deleted
        String sql = "DELETE FROM vendor_services WHERE id = ?"; 
        System.out.println("VendorServiceDAO: Deleting (hard) service ID: " + serviceId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;
            System.out.println("VendorServiceDAO: Delete service ID " + serviceId + (success ? " successful." : " failed."));
            return success;
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.deleteService for serviceId " + serviceId + ": " + e.getMessage());
            throw e;
        }
    }

    private VendorService mapResultSetToVendorService(ResultSet rs) throws SQLException {
        VendorService service = new VendorService();
        service.setId(rs.getInt("id"));
        service.setVendorId(rs.getInt("vendor_id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setServiceType(rs.getString("service_type"));
        service.setPrice(rs.getDouble("price"));
        service.setLocation(rs.getString("location"));
        service.setImageUrl(rs.getString("image_url"));
        service.setActive(rs.getBoolean("is_active"));
        service.setCreatedAt(rs.getTimestamp("created_at"));
        service.setUpdatedAt(rs.getTimestamp("updated_at"));
        return service;
    }
}