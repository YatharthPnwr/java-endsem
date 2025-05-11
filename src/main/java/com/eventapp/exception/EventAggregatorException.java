package com.eventapp.exception;

/**
 * General exception for the Event Aggregator application.
 */
public class EventAggregatorException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public EventAggregatorException() {
        super("An error occurred in the Event Aggregator application");
    }
    
    public EventAggregatorException(String message) {
        super(message);
    }
    
    public EventAggregatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
