package com.eventapp.model;

import java.io.Serializable;
import java.util.Date; // Use java.util.Date
import java.util.List;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private Date eventDate;
    private String eventTime; // <-- ADDED
    private String eventLocation;
    private String status; 
    private double totalCost;
    private Date bookingDate; // This can serve as 'createdAt'
    private List<Integer> vendorIds; 
    
    public Booking() {
    }
    
    // Parameterized constructor (update if needed)
    public Booking(int id, int userId, Date eventDate, String eventTime, String eventLocation, 
                  String status, double totalCost, Date bookingDate, List<Integer> vendorIds) {
        this.id = id;
        this.userId = userId;
        this.eventDate = eventDate;
        this.eventTime = eventTime; // <-- ADDED
        this.eventLocation = eventLocation;
        this.status = status;
        this.totalCost = totalCost;
        this.bookingDate = bookingDate;
        this.vendorIds = vendorIds;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; } // <-- ADDED
    public void setEventTime(String eventTime) { this.eventTime = eventTime; } // <-- ADDED
    
    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    
    public Date getBookingDate() { return bookingDate; } // This is your 'createdAt'
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; } // Setter for 'createdAt'
    
    public List<Integer> getVendorIds() { return vendorIds; }
    public void setVendorIds(List<Integer> vendorIds) { this.vendorIds = vendorIds; }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventDate=" + eventDate +
                ", eventTime='" + eventTime + '\'' + // <-- ADDED
                ", eventLocation='" + eventLocation + '\'' +
                ", status='" + status + '\'' +
                ", totalCost=" + totalCost +
                ", bookingDate=" + bookingDate +
                '}';
    }
}