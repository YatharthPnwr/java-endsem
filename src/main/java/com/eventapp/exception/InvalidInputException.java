package com.eventapp.exception;

/**
 * Custom exception for handling invalid user inputs.
 */
public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public InvalidInputException() {
        super("Invalid input provided");
    }
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}