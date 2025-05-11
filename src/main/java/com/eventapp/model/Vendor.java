package com.eventapp.model;

import java.io.Serializable;

/**
 * Represents a vendor service provider in the Event Aggregator application.
 */
public class Vendor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;  // Added userId field to link to User accounts
    private String name;
    private String type;        // Type of service (e.g. Catering, Photography)
    private String description;
    private String contactInfo;
    private String location;    // Added location field
    private double baseCost;
    private String imageUrl;    // Path to vendor profile/service image
    private boolean isActive;   // Added isActive field
    private double rating;      // Added rating field
    
    // Default constructor
    public Vendor() {
        this.rating = 0.0;
    }
    
    // Parameterized constructor
    public Vendor(int id, String name, String type, String description, 
                 String contactInfo, double baseCost, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.contactInfo = contactInfo;
        this.baseCost = baseCost;
        this.imageUrl = imageUrl;
        this.isActive = true;
        this.rating = 0.0;
    }
    
    // Extended constructor with all fields
    public Vendor(int id, int userId, String name, String type, String description, 
                 String contactInfo, String location, double baseCost, String imageUrl, boolean isActive, double rating) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.contactInfo = contactInfo;
        this.location = location;
        this.baseCost = baseCost;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.rating = rating;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public double getBaseCost() {
        return baseCost;
    }
    
    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
    
    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", location='" + location + '\'' +
                ", baseCost=" + baseCost +
                ", isActive=" + isActive +
                ", rating=" + rating +
                '}';
    }
}