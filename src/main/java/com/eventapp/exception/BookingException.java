package com.eventapp.exception;

/**
 * Custom exception for handling booking-related errors.
 */
public class BookingException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public BookingException() {
        super("A booking error occurred");
    }
    
    public BookingException(String message) {
        super(message);
    }
    
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
