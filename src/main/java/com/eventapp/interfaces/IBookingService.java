package com.eventapp.interfaces;

import com.eventapp.model.Booking;
import java.util.Date;
import java.util.List;

/**
 * Interface defining booking-related operations.
 */
public interface IBookingService {
    
    /**
     * Creates a new booking.
     * 
     * @param userId User making the booking
     * @param eventDate Date of the event
     * @param eventLocation Location of the event
     * @param vendorIds List of vendor IDs selected for this booking
     * @return The created booking ID
     */
    int createBooking(int userId, Date eventDate, String eventLocation, List<Integer> vendorIds);
    
    /**
     * Retrieves a booking by ID.
     * 
     * @param bookingId ID of the booking to retrieve
     * @return The booking object
     */
    Booking getBookingById(int bookingId);
    
    /**
     * Gets all bookings for a specific user.
     * 
     * @param userId ID of the user
     * @return List of bookings for the user
     */
    List<Booking> getBookingsByUser(int userId);
    
    /**
     * Updates the status of a booking.
     * 
     * @param bookingId ID of the booking
     * @param status New status value
     * @return true if update was successful
     */
    boolean updateBookingStatus(int bookingId, String status);
    
    /**
     * Cancels a booking.
     * 
     * @param bookingId ID of the booking to cancel
     * @return true if cancellation was successful
     */
    boolean cancelBooking(int bookingId);
    
    /**
     * Gets all bookings for a specific vendor.
     * 
     * @param vendorId ID of the vendor
     * @return List of bookings that include this vendor
     */
    List<Booking> getBookingsByVendor(int vendorId);
}
