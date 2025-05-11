package com.eventapp.dao;

import com.eventapp.model.Vendor; // Make sure this import is present
import com.eventapp.model.VendorService;
import com.eventapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // <<< --- ADDED THIS IMPORT
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorServiceDAO {

    private String getServiceOrderByClause(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return " ORDER BY vs.name ASC"; 
        }
        Map<String, String> allowedSorts = new HashMap<>();
        allowedSorts.put("price_asc", "vs.price ASC");
        allowedSorts.put("price_desc", "vs.price DESC");
        allowedSorts.put("name_asc", "vs.name ASC");
        allowedSorts.put("name_desc", "vs.name DESC");
        String orderBy = allowedSorts.get(sortParam.toLowerCase());
        return (orderBy != null) ? " ORDER BY " + orderBy : " ORDER BY vs.name ASC"; 
    }

    public List<VendorService> findAllActiveServicesWithVendorDetails(String serviceTypeFilter, String searchQuery, String sortParam) throws SQLException {
        List<VendorService> servicesWithVendor = new ArrayList<>();
        String orderByClause = getServiceOrderByClause(sortParam);

        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT vs.*, v.business_name, v.service_type as vendor_main_type, v.location as vendor_location, v.contact_email, v.contact_phone, v.rating as vendor_rating, v.image_url as vendor_image_url, v.user_id as vendor_user_id " +
            "FROM vendor_services vs " +
            "JOIN vendors v ON vs.vendor_id = v.id " +
            "WHERE vs.is_active = 1 AND v.is_active = 1" 
        );

        List<Object> params = new ArrayList<>();

        if (serviceTypeFilter != null && !serviceTypeFilter.trim().isEmpty()) {
            sqlBuilder.append(" AND vs.service_type = ?"); 
            params.add(serviceTypeFilter);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sqlBuilder.append(" AND (vs.name LIKE ? OR vs.description LIKE ? OR v.business_name LIKE ?)");
            params.add("%" + searchQuery + "%");
            params.add("%" + searchQuery + "%");
            params.add("%" + searchQuery + "%");
        }
        sqlBuilder.append(orderByClause);

        String sql = sqlBuilder.toString();
        System.out.println("VendorServiceDAO: SQL for findAllActiveServicesWithVendorDetails: " + sql);

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    VendorService service = mapResultSetToVendorService(resultSet);
                    Vendor vendor = new Vendor();
                    vendor.setId(service.getVendorId()); 
                    vendor.setUserId(resultSet.getInt("vendor_user_id"));
                    vendor.setName(resultSet.getString("business_name"));
                    vendor.setType(resultSet.getString("vendor_main_type")); 
                    vendor.setLocation(resultSet.getString("vendor_location"));
                    vendor.setContactInfo(resultSet.getString("contact_email") + 
                                          (resultSet.getString("contact_phone") != null ? ", " + resultSet.getString("contact_phone") : ""));
                    vendor.setRating(resultSet.getDouble("vendor_rating"));
                    vendor.setImageUrl(resultSet.getString("vendor_image_url"));
                    vendor.setActive(true); 

                    service.setVendor(vendor); // Call the setter
                    servicesWithVendor.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.findAllActiveServicesWithVendorDetails: " + e.getMessage());
            throw e;
        }
        System.out.println("VendorServiceDAO: Found " + servicesWithVendor.size() + " active services with vendor details.");
        return servicesWithVendor;
    }
    
    public List<VendorService> getActiveServicesByVendorId(int vendorId) throws SQLException {
        List<VendorService> services = new ArrayList<>();
        String sql = "SELECT vs.*, v.business_name " + 
                     "FROM vendor_services vs " +
                     "JOIN vendors v ON vs.vendor_id = v.id " +
                     "WHERE vs.vendor_id = ? AND vs.is_active = 1 AND v.is_active = 1 ORDER BY vs.name";
        System.out.println("VendorServiceDAO: Fetching ACTIVE services for vendor ID: " + vendorId + " using SQL: " + sql);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vendorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    VendorService service = mapResultSetToVendorService(resultSet);
                    Vendor parentVendor = new Vendor();
                    parentVendor.setName(resultSet.getString("business_name"));
                    service.setVendor(parentVendor); // Call the setter
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.getActiveServicesByVendorId for vendorId " + vendorId + ": " + e.getMessage());
            throw e;
        }
        System.out.println("VendorServiceDAO: Found " + services.size() + " ACTIVE services for vendor ID: " + vendorId);
        return services;
    }

    public List<VendorService> getAllServicesByVendorIdForManagement(int vendorId) throws SQLException {
        List<VendorService> services = new ArrayList<>();
        String sql = "SELECT * FROM vendor_services WHERE vendor_id = ? ORDER BY name";
        System.out.println("VendorServiceDAO: Fetching ALL services (for management) for vendor ID: " + vendorId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vendorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    services.add(mapResultSetToVendorService(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in VendorServiceDAO.getAllServicesByVendorIdForManagement for vendorId " + vendorId + ": " + e.getMessage());
            throw e;
        }
        System.out.println("VendorServiceDAO: Found " + services.size() + " total services (for management) for vendor ID: " + vendorId);
        return services;
    }


    public VendorService getServiceById(int serviceId) throws SQLException {
        VendorService service = null;
        String sql = "SELECT * FROM vendor_services WHERE id = ?";
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
        System.out.println("VendorServiceDAO: Adding new service: '" + service.getName() + "' for vendor ID: " + service.getVendorId() + ", Active: " + service.isActive());
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
            System.err.println("SQLException in VendorServiceDAO.addService for service '" + service.getName() + "': " + e.getMessage());
            throw e;
        }
    }

    public boolean updateService(VendorService service) throws SQLException {
        String sql = "UPDATE vendor_services SET name = ?, description = ?, service_type = ?, price = ?, location = ?, image_url = ?, is_active = ?, updated_at = NOW() WHERE id = ? AND vendor_id = ?";
        System.out.println("VendorServiceDAO: Updating service ID: " + service.getId() + ", Active: " + service.isActive());
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

    public List<String> findDistinctServiceTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT service_type FROM vendor_services WHERE is_active = 1 ORDER BY service_type";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement(); // Correctly imported java.sql.Statement
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                types.add(resultSet.getString("service_type"));
            }
        }
        return types;
    }
}