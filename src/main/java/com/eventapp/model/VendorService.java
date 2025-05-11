package com.eventapp.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class representing a service offered by a vendor.
 */
public class VendorService {
    private int id;
    private int vendorId;
    private String name;
    private String description;
    private String serviceType;
    private double price;
    private String location;
    private String imageUrl;
    private boolean active = true;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor
     */
    public VendorService() {
    }

    /**
     * Constructor with essential fields
     * 
     * @param id The service ID
     * @param vendorId The vendor ID who owns this service
     * @param name The service name
     * @param description The service description
     * @param serviceType The type of service
     * @param price The price of the service
     */
    public VendorService(int id, int vendorId, String name, String description, String serviceType, double price) {
        this.id = id;
        this.vendorId = vendorId;
        this.name = name;
        this.description = description;
        this.serviceType = serviceType;
        this.price = price;
    }

    /**
     * Full constructor
     * 
     * @param id The service ID
     * @param vendorId The vendor ID who owns this service
     * @param name The service name
     * @param description The service description
     * @param serviceType The type of service
     * @param price The price of the service
     * @param location The service location
     * @param imageUrl The URL for the service image
     * @param active Whether the service is currently active
     * @param createdAt When the service was created
     * @param updatedAt When the service was last updated
     */
    public VendorService(int id, int vendorId, String name, String description, String serviceType, double price,
            String location, String imageUrl, boolean active, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.vendorId = vendorId;
        this.name = name;
        this.description = description;
        this.serviceType = serviceType;
        this.price = price;
        this.location = location;
        this.imageUrl = imageUrl;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "VendorService [id=" + id + ", vendorId=" + vendorId + ", name=" + name + ", serviceType=" + serviceType
                + ", price=" + price + ", active=" + active + "]";
    }
}