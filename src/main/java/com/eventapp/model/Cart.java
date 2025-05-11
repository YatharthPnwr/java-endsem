package com.eventapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping cart for selecting vendor services.
 */
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private List<Integer> vendorIds;
    private double totalCost;
    
    // Default constructor
    public Cart() {
        this.vendorIds = new ArrayList<>();
        this.totalCost = 0.0;
    }
    
    // Parameterized constructor
    public Cart(int userId) {
        this.userId = userId;
        this.vendorIds = new ArrayList<>();
        this.totalCost = 0.0;
    }
    
    // Getters and setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public List<Integer> getVendorIds() {
        return vendorIds;
    }
    
    public void setVendorIds(List<Integer> vendorIds) {
        this.vendorIds = vendorIds;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    // Helper methods
    public void addVendor(int vendorId, double cost) {
        this.vendorIds.add(vendorId);
        this.totalCost += cost;
    }
    
    public void removeVendor(int vendorId, double cost) {
        this.vendorIds.remove(Integer.valueOf(vendorId));
        this.totalCost -= cost;
    }
    
    public void clear() {
        this.vendorIds.clear();
        this.totalCost = 0.0;
    }
    
    public int getItemCount() {
        return this.vendorIds.size();
    }
    
    @Override
    public String toString() {
        return "Cart{" +
                "userId=" + userId +
                ", vendorIds=" + vendorIds +
                ", totalCost=" + totalCost +
                '}';
    }
}