package com.eventapp.interfaces;

import com.eventapp.model.Vendor;
import java.util.List;

/**
 * Interface defining vendor management operations.
 */
public interface IVendorManager {
    
    /**
     * Registers a new vendor in the system.
     * 
     * @param vendor The vendor to register
     * @return The ID of the registered vendor
     */
    int registerVendor(Vendor vendor);
    
    /**
     * Updates vendor information.
     * 
     * @param vendor The updated vendor information
     * @return true if update was successful
     */
    boolean updateVendor(Vendor vendor);
    
    /**
     * Retrieves a vendor by ID.
     * 
     * @param vendorId ID of the vendor
     * @return The vendor object
     */
    Vendor getVendorById(int vendorId);
    
    /**
     * Gets all vendors in the system.
     * 
     * @return List of all vendors
     */
    List<Vendor> getAllVendors();
    
    /**
     * Gets vendors by service type.
     * 
     * @param type Type of service
     * @return List of vendors matching the service type
     */
    List<Vendor> getVendorsByType(String type);
    
    /**
     * Removes a vendor from the system.
     * 
     * @param vendorId ID of the vendor to remove
     * @return true if removal was successful
     */
    boolean deleteVendor(int vendorId);
}