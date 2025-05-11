package com.eventapp.model;

import java.sql.Timestamp;
// No need for java.util.Date here if using Timestamp consistently from DB

public class VendorService {
    private int id;
    private int vendorId; // Foreign key to vendors table
    private String name;
    private String description;
    private String serviceType; // Specific type of this service (e.g., "Basic Photography Package")
    private double price;
    private String location; // Specific location for this service, if different from vendor's main
    private String imageUrl;
    private boolean active = true;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // To hold parent vendor details when joining
    private Vendor vendor; 

    public VendorService() {
    }

    // Getters and Setters for all fields including 'vendor'
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Vendor getVendor() { return vendor; } // Getter for Vendor
    public void setVendor(Vendor vendor) { this.vendor = vendor; } // Setter for Vendor

    @Override
    public String toString() {
        return "VendorService [id=" + id + ", vendorId=" + vendorId + ", name=" + name + ", serviceType=" + serviceType
                + ", price=" + price + ", active=" + active + "]";
    }
}