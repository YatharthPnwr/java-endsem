package com.eventapp.dao;

import com.eventapp.model.Booking;
import com.eventapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingDAO {

    public List<Booking> getBookingsByVendorId(int vendorId) throws SQLException {
        // ... (as previously provided, ensure it works with your schema) ...
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as user_name FROM bookings b " +
                     "JOIN booking_vendors bv ON b.id = bv.booking_id " +
                     "JOIN users u ON b.user_id = u.id " + // Join with users table
                     "WHERE bv.vendor_id = ? ORDER BY b.event_date DESC";
        if (columnExistsInTable("bookings", "event_time")) { // Check if event_time column exists
             sql = "SELECT b.*, u.name as user_name FROM bookings b " +
                   "JOIN booking_vendors bv ON b.id = bv.booking_id " +
                   "JOIN users u ON b.user_id = u.id " +
                   "WHERE bv.vendor_id = ? ORDER BY b.event_date DESC, b.event_time ASC";
        }
        
        System.out.println("BookingDAO: Fetching bookings for vendor ID: " + vendorId);

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vendorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = extractBookingFromResultSet(resultSet); // Use helper
                    // booking.setUserName(resultSet.getString("user_name")); // If you add userName to Booking model
                    booking.setVendorIds(getVendorIdsForBooking(connection, booking.getId())); // Populate vendor IDs
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in BookingDAO.getBookingsByVendorId for vendorId " + vendorId + ": " + e.getMessage());
            e.printStackTrace(); 
            throw e; 
        }
        System.out.println("BookingDAO: Found " + bookings.size() + " bookings for vendor ID: " + vendorId);
        return bookings;
    }

    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC";
         System.out.println("BookingDAO: Fetching bookings for user ID: " + userId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = extractBookingFromResultSet(resultSet);
                    booking.setVendorIds(getVendorIdsForBooking(connection, booking.getId()));
                    bookings.add(booking);
                }
            }
        }
        System.out.println("BookingDAO: Found " + bookings.size() + " bookings for user ID: " + userId);
        return bookings;
    }

    public Booking findById(int bookingId) throws SQLException {
        Booking booking = null;
        String sql = "SELECT * FROM bookings WHERE id = ?";
        System.out.println("BookingDAO: Fetching booking by ID: " + bookingId);
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    booking = extractBookingFromResultSet(resultSet);
                    booking.setVendorIds(getVendorIdsForBooking(connection, bookingId));
                }
            }
        }
        System.out.println("BookingDAO: Booking " + (booking != null ? "found" : "not found") + " for ID: " + bookingId);
        return booking;
    }
    
    // Helper method to get vendor IDs for a specific booking (can be called with an existing connection)
    private List<Integer> getVendorIdsForBooking(Connection conn, int bookingId) throws SQLException {
        List<Integer> vendorIds = new ArrayList<>();
        String sql = "SELECT vendor_id FROM booking_vendors WHERE booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vendorIds.add(rs.getInt("vendor_id"));
                }
            }
        }
        return vendorIds;
    }

    public int addBooking(Booking booking) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        int bookingId = -1;

        // Ensure bookings table has event_time if your model uses it
        String sqlBookings = "INSERT INTO bookings (user_id, event_date, event_time, event_location, status, total_cost, booking_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        if (!columnExistsInTable("bookings", "event_time")) {
            sqlBookings = "INSERT INTO bookings (user_id, event_date, event_location, status, total_cost, booking_date) VALUES (?, ?, ?, ?, ?, ?)";
        }


        System.out.println("BookingDAO: Adding booking for user " + booking.getUserId());
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false); // Start transaction

            statement = connection.prepareStatement(sqlBookings, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, booking.getUserId());
            statement.setDate(2, new java.sql.Date(booking.getEventDate().getTime()));
            
            int paramIndex = 3;
            if (columnExistsInTable("bookings", "event_time")) {
                statement.setString(paramIndex++, booking.getEventTime());
            }
            statement.setString(paramIndex++, booking.getEventLocation());
            statement.setString(paramIndex++, booking.getStatus());
            statement.setDouble(paramIndex++, booking.getTotalCost());
            statement.setTimestamp(paramIndex++, new java.sql.Timestamp(booking.getBookingDate().getTime()));
            
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bookingId = generatedKeys.getInt(1);
                    System.out.println("BookingDAO: Booking record created with ID: " + bookingId);
                    // Add to booking_vendors table
                    addVendorsToBookingTransaction(connection, bookingId, booking.getVendorIds());
                    connection.commit();
                    System.out.println("BookingDAO: Transaction committed for booking ID " + bookingId);
                } else {
                    connection.rollback();
                    System.err.println("BookingDAO: Failed to get generated key for booking.");
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            } else {
                connection.rollback();
                 System.err.println("BookingDAO: Creating booking failed, no rows affected.");
                throw new SQLException("Creating booking failed, no rows affected.");
            }
        } catch (SQLException e) {
            if (connection != null) try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("SQLException in BookingDAO.addBooking: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (statement != null) try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (connection != null) try { connection.setAutoCommit(true); connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return bookingId;
    }

    // Helper for addBooking within a transaction
    private void addVendorsToBookingTransaction(Connection connection, int bookingId, List<Integer> vendorIds) throws SQLException {
        String sql = "INSERT INTO booking_vendors (booking_id, vendor_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Integer vendorId : vendorIds) {
                statement.setInt(1, bookingId);
                statement.setInt(2, vendorId);
                statement.addBatch();
            }
            statement.executeBatch();
            System.out.println("BookingDAO: Added " + vendorIds.size() + " vendors to booking_vendors for booking ID " + bookingId);
        }
    }

    public boolean updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, bookingId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setEventDate(rs.getDate("event_date"));
        if (columnExists(rs, "event_time")) { // Check if column exists before reading
            booking.setEventTime(rs.getString("event_time"));
        } else {
            booking.setEventTime("N/A"); // Or null, or some default
        }
        booking.setEventLocation(rs.getString("event_location"));
        booking.setStatus(rs.getString("status"));
        booking.setTotalCost(rs.getDouble("total_cost"));
        booking.setBookingDate(rs.getTimestamp("booking_date")); // Use booking_date from DB
        return booking;
    }
    
    // Utility to check if a column exists in the ResultSet (to avoid errors if schema is inconsistent)
    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            // Column does not exist
            return false;
        }
    }
    // Utility to check if a column exists in a table (more robust but requires another query)
    private boolean columnExistsInTable(String tableName, String columnName) {
        try (Connection conn = DatabaseUtil.getConnection();
             ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume not if error
        }
    }
}