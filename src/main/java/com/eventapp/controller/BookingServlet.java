package com.eventapp.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventapp.exception.BookingException;
import com.eventapp.model.Booking;
import com.eventapp.model.Cart;
import com.eventapp.model.User;
import com.eventapp.model.Vendor;
import com.eventapp.util.DatabaseUtil;

/**
 * Servlet for handling booking operations.
 */
@WebServlet(urlPatterns = {
    "/checkout", 
    "/user/bookings", 
    "/booking/details",  // Example: if you have a specific details page
    "/booking/confirm",  // Example: for confirming a booking
    "/booking/process-payment", // Example
    "/cancel-booking"
    // Add any other booking-specific URLs here, but NOT /cart
})
public class BookingServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        System.out.println("Request URI: " + requestURI);
        System.out.println("Context Path: " + contextPath);
        System.out.println("Servlet Path: " + path);
        System.out.println("Path Info: " + request.getPathInfo());
        
        // Check if user is logged in for all booking-related operations
        if (!checkLogin(request, response)) {
            return;
        }
        
        if ("/cart".equals(path)) {
            handleViewCart(request, response);
        } else if ("/checkout".equals(path)) {
            handleCheckoutForm(request, response);
        } else if ("/user/bookings".equals(path)) {
            handleViewBookings(request, response);
        } else if (path.startsWith("/booking")) {
            // Special handling for booking details
            if (requestURI.length() > contextPath.length() + "/booking".length()) {
                String[] parts = requestURI.substring(contextPath.length() + "/booking".length() + 1).split("/");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    try {
                        int bookingId = Integer.parseInt(parts[0]);
                        handleViewBookingDetails(request, response, bookingId);
                        return;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid booking ID format: " + parts[0]);
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/user/bookings");
        } else {
            System.out.println("Unknown path: " + path);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        // Check if user is logged in for all booking-related operations
        if (!checkLogin(request, response)) {
            return;
        }
        
        if ("/checkout".equals(path)) {
            handleCreateBooking(request, response);
        } else if ("/cancel-booking".equals(path)) {
            handleCancelBooking(request, response);
        }
    }
    
    /**
     * Handles viewing the shopping cart.
     */
    private void handleViewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = getLoggedInUser(request);
        Cart cart = (Cart) session.getAttribute("cart");
        
        // Initialize cart if it doesn't exist in session
        if (cart == null) {
            cart = new Cart(user.getId());
            session.setAttribute("cart", cart);
        }
        
        // Get cart items from the database instead of relying solely on session data
        com.eventapp.dao.CartDAO cartDAO = new com.eventapp.dao.CartDAO();
        List<Integer> dbVendorIds = cartDAO.getCartItems(user.getId());
        
        // Get details for each vendor in the cart from the database
        List<Vendor> cartVendors = new ArrayList<>();
        double totalCost = 0.0;
        
        for (Integer vendorId : dbVendorIds) {
            Vendor vendor = getVendorById(vendorId);
            if (vendor != null) {
                cartVendors.add(vendor);
                totalCost += vendor.getBaseCost();
            }
        }
        
        // Synchronize session cart with database cart
        if (!dbVendorIds.isEmpty()) {
            cart.setVendorIds(new ArrayList<>(dbVendorIds)); // Set vendor IDs from DB
            cart.setTotalCost(totalCost); // Set total cost based on vendors' prices
            
            // Update the session
            session.setAttribute("cart", cart);
        }
        
        if (dbVendorIds.isEmpty()) {
            // Empty cart
            request.setAttribute("message", "Your cart is empty. Browse services to add to your cart.");
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
            return;
        }
        
        // Set attributes for the view
        request.setAttribute("cartVendors", cartVendors);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }
    
    /**
     * Handles displaying the checkout form.
     */
    private void handleCheckoutForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        
        if (cart == null || cart.getVendorIds().isEmpty()) {
            // Redirect to cart if empty
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        // Get details for each vendor in the cart for display
        List<Vendor> cartVendors = new ArrayList<>();
        for (Integer vendorId : cart.getVendorIds()) {
            Vendor vendor = getVendorById(vendorId);
            if (vendor != null) {
                cartVendors.add(vendor);
            }
        }
        
        request.setAttribute("cartVendors", cartVendors);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }
    
    /**
     * Handles creating a new booking from checkout form.
     */
    private void handleCreateBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        User user = getLoggedInUser(request);
        
        if (cart == null || cart.getVendorIds().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        String eventDateStr = request.getParameter("eventDate");
        String eventLocation = request.getParameter("eventLocation");
        
        try {
            // Validate inputs
            if (eventDateStr == null || eventDateStr.trim().isEmpty()) {
                throw new BookingException("Event date is required.");
            }
            if (eventLocation == null || eventLocation.trim().isEmpty()) {
                throw new BookingException("Event location is required.");
            }
            
            // Parse event date
            Date eventDate;
            try {
                eventDate = DATE_FORMAT.parse(eventDateStr);
            } catch (ParseException e) {
                throw new BookingException("Invalid event date format. Please use YYYY-MM-DD.");
            }
            
            // Create the booking
            int bookingId = createBooking(user.getId(), eventDate, eventLocation, cart.getVendorIds(), cart.getTotalCost());
            
            if (bookingId > 0) {
                // Clear the cart after successful booking
                cart.clear();
                session.setAttribute("cart", cart);
                
                // Show success message and redirect to booking details
                request.setAttribute("successMessage", "Booking created successfully!");
                response.sendRedirect(request.getContextPath() + "/user/bookings");
            } else {
                throw new BookingException("Failed to create booking. Please try again.");
            }
        } catch (BookingException e) {
            request.setAttribute("errorMessage", e.getMessage());
            handleCheckoutForm(request, response);
        }
    }
    
    /**
     * Handles viewing all bookings for the logged-in user.
     */
    private void handleViewBookings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        List<Booking> bookings = getBookingsByUser(user.getId());
        
        // Add current date for comparing with event dates (to determine if cancellation is allowed)
        request.setAttribute("now", new Date());
        
        // Create a map of all vendors used in these bookings for printing invoice details
        List<Vendor> vendors = new ArrayList<>();
        for (Booking booking : bookings) {
            for (Integer vendorId : booking.getVendorIds()) {
                Vendor vendor = getVendorById(vendorId);
                if (vendor != null) {
                    vendors.add(vendor);
                }
            }
        }
        request.setAttribute("vendors", vendors);
        
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/WEB-INF/views/bookings.jsp").forward(request, response);
    }
    
    /**
     * Handles viewing details for a specific booking.
     */
    private void handleViewBookingDetails(HttpServletRequest request, HttpServletResponse response, int bookingId)
            throws ServletException, IOException {
                System.out.println("Handling view booking details for ID: " + bookingId); // Debug statement
        
                Booking booking = getBookingById(bookingId);
                
                if (booking != null) {
                    System.out.println("Booking found: " + booking.getId()); // Debug statement
                    User user = getLoggedInUser(request);
                    
                    // Ensure user can only view their own bookings (or is admin)
                    if (booking.getUserId() == user.getId() || isAdmin(request)) {
                        // Add current date for comparing with event dates (to determine if cancellation is allowed)
                        request.setAttribute("now", new Date());
                        
                        // Get details of vendors in this booking
                        List<Vendor> bookingVendors = new ArrayList<>();
                        for (Integer vendorId : booking.getVendorIds()) {
                            Vendor vendor = getVendorById(vendorId);
                            if (vendor != null) {
                                bookingVendors.add(vendor);
                            }
                        }
                        
                        System.out.println("Setting booking attributes: id=" + booking.getId() + ", vendors=" + bookingVendors.size()); // Debug statement
                        request.setAttribute("booking", booking);
                        request.setAttribute("bookingVendors", bookingVendors);
                        
                        // Use the full booking details JSP
                        String jspPath = "/WEB-INF/views/booking-details.jsp";
                        System.out.println("Forwarding to JSP: " + jspPath); // Debug statement
                        
                        try {
                            request.getRequestDispatcher(jspPath).forward(request, response);
                            System.out.println("Forward to JSP completed"); // Debug statement
                            return;
                        } catch (Exception e) {
                            System.out.println("Error forwarding to JSP: " + e.getMessage()); // Debug statement
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Authorization failed. User ID: " + user.getId() + ", Booking user ID: " + booking.getUserId()); // Debug statement
                    }
                } else {
                    System.out.println("Booking not found with ID: " + bookingId); // Debug statement
                }
        
        // Booking not found or unauthorized
        System.out.println("Redirecting to bookings page"); // Debug statement
        response.sendRedirect(request.getContextPath() + "/user/bookings");
    }
    
    /**
     * Handles canceling a booking.
     */
    private void handleCancelBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookingIdStr = request.getParameter("bookingId");
        
        if (bookingIdStr != null && !bookingIdStr.isEmpty()) {
            try {
                int bookingId = Integer.parseInt(bookingIdStr);
                Booking booking = getBookingById(bookingId);
                
                if (booking != null) {
                    User user = getLoggedInUser(request);
                    
                    // Ensure user can only cancel their own bookings (or is admin)
                    if (booking.getUserId() == user.getId() || isAdmin(request)) {
                        // Check if event date is in the future
                        Date today = new Date();
                        if (booking.getEventDate().after(today)) {
                            if (cancelBooking(bookingId)) {
                                request.setAttribute("successMessage", "Booking canceled successfully.");
                            } else {
                                request.setAttribute("errorMessage", "Failed to cancel booking. Please try again.");
                            }
                        } else {
                            request.setAttribute("errorMessage", "Cannot cancel past or ongoing events.");
                        }
                    } else {
                        request.setAttribute("errorMessage", "You are not authorized to cancel this booking.");
                    }
                } else {
                    request.setAttribute("errorMessage", "Booking not found.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid booking ID.");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/user/bookings");
    }
    
    /**
     * Creates a new booking in the database.
     */
    private int createBooking(int userId, Date eventDate, String eventLocation, List<Integer> vendorIds, double totalCost) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Insert the booking record
            String sql = "INSERT INTO bookings (user_id, event_date, event_location, status, total_cost, booking_date) " +
                         "VALUES (?, ?, ?, ?, ?, NOW())";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setDate(2, new java.sql.Date(eventDate.getTime()));
            statement.setString(3, eventLocation);
            statement.setString(4, "PENDING");
            statement.setDouble(5, totalCost);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int bookingId = generatedKeys.getInt(1);
                    
                    // Insert booking-vendor relationships
                    addVendorsToBooking(connection, bookingId, vendorIds);
                    
                    connection.commit();
                    return bookingId;
                }
            }
            
            connection.rollback();
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Adds vendor associations to a booking.
     */
    private boolean addVendorsToBooking(Connection connection, int bookingId, List<Integer> vendorIds) throws SQLException {
        PreparedStatement statement = null;
        
        try {
            String sql = "INSERT INTO booking_vendors (booking_id, vendor_id) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            
            for (Integer vendorId : vendorIds) {
                statement.setInt(1, bookingId);
                statement.setInt(2, vendorId);
                statement.addBatch();
            }
            
            int[] results = statement.executeBatch();
            for (int result : results) {
                if (result < 0) {
                    return false;
                }
            }
            
            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }
    
    /**
     * Gets a booking by ID.
     */
    private Booking getBookingById(int bookingId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM bookings WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, bookingId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Booking booking = extractBookingFromResultSet(resultSet);
                booking.setVendorIds(getVendorIdsForBooking(bookingId));
                return booking;
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
        
        return null;
    }
    
    /**
     * Gets all bookings for a specific user.
     */
    private List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Booking booking = extractBookingFromResultSet(resultSet);
                booking.setVendorIds(getVendorIdsForBooking(booking.getId()));
                bookings.add(booking);
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
        
        return bookings;
    }
    
    /**
     * Gets vendor IDs associated with a booking.
     */
    private List<Integer> getVendorIdsForBooking(int bookingId) {
        List<Integer> vendorIds = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT vendor_id FROM booking_vendors WHERE booking_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, bookingId);
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
     * Cancels a booking by ID.
     */
    private boolean cancelBooking(int bookingId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, bookingId);
            
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
     * Helper method to extract a booking from a result set.
     */
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setEventDate(rs.getDate("event_date"));
        booking.setEventLocation(rs.getString("event_location"));
        booking.setStatus(rs.getString("status"));
        booking.setTotalCost(rs.getDouble("total_cost"));
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        return booking;
    }
    
    /**
     * Retrieves a vendor by ID.
     */
    private Vendor getVendorById(int vendorId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM vendors WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, vendorId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Vendor vendor = new Vendor();
                vendor.setId(resultSet.getInt("id"));
                vendor.setName(resultSet.getString("business_name"));
                vendor.setType(resultSet.getString("service_type"));
                vendor.setDescription(resultSet.getString("description"));
                
                // Handle contact info properly
                try {
                    String contactEmail = resultSet.getString("contact_email");
                    String contactPhone = resultSet.getString("contact_phone");
                    vendor.setContactInfo(contactEmail + " | " + contactPhone);
                } catch (SQLException e) {
                    // If the specific columns don't exist, try the combined column as fallback
                    try {
                        vendor.setContactInfo(resultSet.getString("contact_info"));
                    } catch (SQLException ex) {
                        // If neither approach works, set a default value
                        vendor.setContactInfo("Contact information not available");
                    }
                }
                
                vendor.setBaseCost(resultSet.getDouble("base_cost"));
                vendor.setImageUrl(resultSet.getString("image_url"));
                return vendor;
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
        
        return null;
    }
}