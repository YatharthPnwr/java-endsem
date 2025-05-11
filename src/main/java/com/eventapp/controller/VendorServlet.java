package com.eventapp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventapp.dao.BookingDAO;
import com.eventapp.dao.UserDAO;
import com.eventapp.dao.VendorDAO;
import com.eventapp.dao.VendorServiceDAO;
import com.eventapp.exception.EventAggregatorException;
import com.eventapp.exception.InvalidInputException;
import com.eventapp.model.AdminVendorView;
import com.eventapp.model.Booking;
import com.eventapp.model.User;
import com.eventapp.model.Vendor;
import com.eventapp.model.VendorService;
import com.eventapp.service.UserService;


@WebServlet(urlPatterns = {
    "/vendors", "/vendor/details", "/vendor/profile/create", "/vendor/profile/edit", "/vendor/profile/save",
    "/vendor-dashboard", "/vendor/services", "/vendor/service/add", "/vendor/service/edit", "/vendor/service/save",
    "/vendor/service/delete", "/admin/vendors", "/admin/vendor/approve", "/admin/vendor/reject"
})
public class VendorServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private VendorDAO vendorDAO;
    private VendorServiceDAO vendorServiceDAO;
    private BookingDAO bookingDAO; // Assuming you have a BookingDAO
    private UserDAO userDAO;       // For checking if user exists for vendor profile
    private UserService userService;

    public VendorServlet() {
        super();
        this.vendorDAO = new VendorDAO();
        this.vendorServiceDAO = new VendorServiceDAO();
        this.bookingDAO = new BookingDAO(); // Initialize BookingDAO
        this.userDAO = new UserDAO();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("VendorServlet - doGet: Path = " + path);

        try {
            switch (path) {
                case "/vendors":
                    listVendors(request, response);
                    break;
                case "/vendor/details":
                    showVendorDetails(request, response);
                    break;
                case "/vendor/profile/create":
                    showCreateVendorProfileForm(request, response);
                    break;
                case "/vendor/profile/edit":
                    showEditVendorProfileForm(request, response);
                    break;
                case "/vendor-dashboard":
                    showVendorDashboard(request, response);
                    break;
                case "/vendor/services": // Might be redundant if part of dashboard
                    listVendorServices(request, response); // For VENDOR to see their own services
                    break;
                case "/vendor/service/add":
                    showAddServiceForm(request, response);
                    break;
                case "/vendor/service/edit":
                    showEditServiceForm(request, response);
                    break;
                case "/admin/vendors":
                    if (isAdmin(request)) {
                        listAllVendorsForAdmin(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/unauthorized");
                    }
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            System.err.println("VendorServlet doGet - SQLException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (EventAggregatorException e) {
            System.err.println("VendorServlet doGet - EventAggregatorException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

            @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("VendorServlet - doPost: Path = " + path);

        try {
            switch (path) {
                case "/vendor/profile/save":
                    saveVendorProfile(request, response);
                    break;
                case "/vendor/service/save":
                    saveVendorService(request, response);
                    break;
                case "/vendor/service/delete":
                    // Assuming deleteVendorService only throws SQLException or nothing critical to catch here
                    deleteVendorService(request, response); 
                    break;
                case "/admin/vendor/approve":
                     if (isAdmin(request)) {
                        approveVendor(request, response); // Assuming only SQLException or nothing critical
                    } else {
                        response.sendRedirect(request.getContextPath() + "/unauthorized");
                    }
                    break;
                case "/admin/vendor/reject":
                     if (isAdmin(request)) {
                        rejectVendor(request, response); // Assuming only SQLException or nothing critical
                    } else {
                        response.sendRedirect(request.getContextPath() + "/unauthorized");
                    }
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            System.err.println("VendorServlet doPost - SQLException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (InvalidInputException e) {
            System.err.println("VendorServlet doPost - InvalidInputException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
            
            if (path.equals("/vendor/profile/save")) {
                User loggedInUser = getLoggedInUser(request);
                Vendor vendorToRepopulate = new Vendor(); 
                vendorToRepopulate.setName(request.getParameter("name"));
                vendorToRepopulate.setType(request.getParameter("type"));
                vendorToRepopulate.setDescription(request.getParameter("description"));
                vendorToRepopulate.setLocation(request.getParameter("location"));
                try {
                    String baseCostParam = request.getParameter("baseCost");
                    if (baseCostParam != null && !baseCostParam.trim().isEmpty()) {
                        vendorToRepopulate.setBaseCost(Double.parseDouble(baseCostParam));
                    }
                } catch (NumberFormatException nfe) { /* ignore, validation should catch this */ }
                vendorToRepopulate.setImageUrl(request.getParameter("imageUrl"));
                if(loggedInUser != null) vendorToRepopulate.setUserId(loggedInUser.getId());

                request.setAttribute("vendor", vendorToRepopulate);
                request.setAttribute("user", loggedInUser);
                request.getRequestDispatcher("/WEB-INF/views/vendor-create-profile.jsp").forward(request, response);

            } else if (path.equals("/vendor/service/save")) {
                User loggedInUser = getLoggedInUser(request);
                Vendor currentVendor = null;
                VendorService serviceToRepopulate = new VendorService();
                try {
                    if(loggedInUser != null) currentVendor = vendorDAO.findByUserId(loggedInUser.getId());

                    serviceToRepopulate.setName(request.getParameter("name"));
                    serviceToRepopulate.setDescription(request.getParameter("description"));
                    serviceToRepopulate.setServiceType(request.getParameter("serviceType"));
                    try {
                        String priceParam = request.getParameter("price");
                        if (priceParam != null && !priceParam.trim().isEmpty()) {
                             serviceToRepopulate.setPrice(Double.parseDouble(priceParam));
                        }
                    } catch (NumberFormatException nfe) { /* ignore */ }
                    serviceToRepopulate.setLocation(request.getParameter("location"));
                    serviceToRepopulate.setImageUrl(request.getParameter("imageUrl"));
                    String serviceIdParam = request.getParameter("id");
                    if (serviceIdParam != null && !serviceIdParam.isEmpty()) {
                        try {
                            serviceToRepopulate.setId(Integer.parseInt(serviceIdParam));
                        } catch (NumberFormatException nfe) { /* ignore */ }
                    }
                } catch (SQLException sqlEx) {
                     System.err.println("VendorServlet doPost (catch InvalidInputException for service) - SQLException: " + sqlEx.getMessage());
                     request.setAttribute("errorMessage", "Error retrieving vendor data after input validation failure for service.");
                }
                request.setAttribute("vendorService", serviceToRepopulate);
                request.setAttribute("vendor", currentVendor);
                request.setAttribute("formAction", (serviceToRepopulate.getId() > 0) ? "edit" : "add"); // Check if ID is set for edit
                request.getRequestDispatcher("/WEB-INF/views/vendor-service-form.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
            }
        // REMOVED the specific catch for EventAggregatorException here as it's not directly thrown by the try block's methods
        // } catch (EventAggregatorException e) { 
        //     System.err.println("VendorServlet doPost - EventAggregatorException: " + e.getMessage());
        //     e.printStackTrace();
        //     request.setAttribute("errorMessage", e.getMessage());
        //     request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (Exception e) { // Generic catch-all for unexpected errors, including any unhandled EventAggregatorException from deeper calls
            System.err.println("VendorServlet doPost - Generic Exception (could be unhandled EventAggregatorException): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }


    private void listVendors(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("VendorServlet: listVendors called");
        String typeFilter = request.getParameter("type");
        List<Vendor> vendors;
        if (typeFilter != null && !typeFilter.trim().isEmpty()) {
            vendors = vendorDAO.findByType(typeFilter);
            request.setAttribute("selectedType", typeFilter);
             System.out.println("VendorServlet: Filtering by type: " + typeFilter + ", Found " + vendors.size() + " vendors.");
        } else {
            vendors = vendorDAO.findAllActive(); // Fetch only active vendors for public listing
            System.out.println("VendorServlet: Fetching all active vendors. Found " + vendors.size() + " vendors.");
        }
        request.setAttribute("vendors", vendors);
        // Get distinct vendor types for filter dropdown
        List<String> vendorTypes = vendorDAO.findDistinctVendorTypes();
        request.setAttribute("vendorTypes", vendorTypes);
        System.out.println("VendorServlet: Forwarding to vendors.jsp");
        request.getRequestDispatcher("/WEB-INF/views/vendors.jsp").forward(request, response);
    }


    private void showVendorDetails(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, EventAggregatorException {
        System.out.println("VendorServlet: showVendorDetails called");
        String idParam = request.getParameter("id");
        if (idParam == null) {
            throw new EventAggregatorException("Vendor ID is required.");
        }
        try {
            int vendorId = Integer.parseInt(idParam);
            Vendor vendor = vendorDAO.findById(vendorId);
            if (vendor == null || !vendor.isActive()) { // Only show active vendor details
                 System.out.println("VendorServlet: Vendor not found or inactive for ID: " + vendorId);
                request.setAttribute("errorMessage", "Vendor not found or is not currently active.");
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
                return;
            }
            List<VendorService> services = vendorServiceDAO.getServicesByVendorId(vendorId);
            request.setAttribute("vendor", vendor);
            request.setAttribute("services", services);
            System.out.println("VendorServlet: Showing details for vendor ID " + vendorId + ". Forwarding to vendor-details.jsp");
            request.getRequestDispatcher("/WEB-INF/views/vendor-details.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            throw new EventAggregatorException("Invalid Vendor ID format.");
        }
    }

    private void showCreateVendorProfileForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET: ENTERING showCreateVendorProfileForm ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            System.out.println("VendorServlet (showCreateVendorProfileForm): User not VENDOR or not logged in. Redirecting.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // Check if vendor profile already exists
        Vendor existingVendor = vendorDAO.findByUserId(sessionUser.getId());
        if (existingVendor != null) {
            System.out.println("VendorServlet (showCreateVendorProfileForm): Vendor profile already exists for user ID " + sessionUser.getId() + ". Redirecting to edit.");
            response.sendRedirect(request.getContextPath() + "/vendor/profile/edit");
            return;
        }
        request.setAttribute("vendor", new Vendor()); // New empty vendor object for the form
        request.setAttribute("user", sessionUser); // Pass user info
        System.out.println("VendorServlet (showCreateVendorProfileForm): Forwarding to vendor-create-profile.jsp");
        request.getRequestDispatcher("/WEB-INF/views/vendor-create-profile.jsp").forward(request, response);
    }

    private void showEditVendorProfileForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET: ENTERING showEditVendorProfileForm ---");
        User sessionUser = getLoggedInUser(request);
         if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        if (vendor == null) {
            // If somehow they reach edit without a profile, redirect to create
            response.sendRedirect(request.getContextPath() + "/vendor/profile/create");
            return;
        }
        request.setAttribute("vendor", vendor);
        request.setAttribute("user", sessionUser);
        System.out.println("VendorServlet (showEditVendorProfileForm): Forwarding to vendor-create-profile.jsp (for editing)");
        request.getRequestDispatcher("/WEB-INF/views/vendor-create-profile.jsp").forward(request, response);
    }


    private void saveVendorProfile(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, InvalidInputException {
        System.out.println("--- VENDOR SERVLET: ENTERING saveVendorProfile ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String vendorIdParam = request.getParameter("id");
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String description = request.getParameter("description");
        String contactInfo = request.getParameter("contactInfo"); // This should ideally be pre-filled from User
        String location = request.getParameter("location");
        String baseCostStr = request.getParameter("baseCost");
        String imageUrl = request.getParameter("imageUrl"); // Optional for now

        // Basic Validation
        if (name == null || name.trim().isEmpty() ||
            type == null || type.trim().isEmpty() ||
            location == null || location.trim().isEmpty() ||
            baseCostStr == null || baseCostStr.trim().isEmpty()) {
            throw new InvalidInputException("Name, Type, Location, and Base Cost are required fields for vendor profile.");
        }
        
        double baseCost;
        try {
            baseCost = Double.parseDouble(baseCostStr);
            if (baseCost < 0) {
                throw new InvalidInputException("Base cost cannot be negative.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid format for Base Cost.");
        }

        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        boolean isNewVendor = (vendor == null);

        if (isNewVendor) {
            vendor = new Vendor();
            vendor.setUserId(sessionUser.getId());
            vendor.setActive(false); // New vendors need admin approval
            System.out.println("VendorServlet (saveVendorProfile): Creating new vendor profile for user ID " + sessionUser.getId());
        } else {
            System.out.println("VendorServlet (saveVendorProfile): Updating existing vendor profile ID " + vendor.getId());
        }

        vendor.setName(name);
        vendor.setType(type);
        vendor.setDescription(description);
        vendor.setContactInfo(sessionUser.getEmail() + (sessionUser.getPhone() != null ? ", " + sessionUser.getPhone() : "")); // Use user's email/phone
        vendor.setLocation(location);
        vendor.setBaseCost(baseCost);
        vendor.setImageUrl(imageUrl); // Handle default if empty
        // vendor.setRating(0.0); // Rating is usually managed by a different process

        boolean success;
        if (isNewVendor) {
            int newVendorId = vendorDAO.add(vendor);
            success = newVendorId > 0;
            if(success) vendor.setId(newVendorId);
        } else {
            success = vendorDAO.update(vendor);
        }

        if (success) {
            System.out.println("VendorServlet (saveVendorProfile): Profile saved successfully. Redirecting to dashboard.");
            request.getSession().setAttribute("successMessage", "Vendor profile " + (isNewVendor ? "created (pending approval)" : "updated") + " successfully!");
            response.sendRedirect(request.getContextPath() + "/vendor-dashboard");
        } else {
            System.err.println("VendorServlet (saveVendorProfile): Failed to save vendor profile.");
            request.setAttribute("errorMessage", "Failed to save vendor profile. Please try again.");
            request.setAttribute("vendor", vendor); // Pass back the vendor object with entered data
            request.setAttribute("user", sessionUser);
            request.getRequestDispatcher("/WEB-INF/views/vendor-create-profile.jsp").forward(request, response);
        }
    }


    private void showVendorDashboard(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {

        System.out.println("--- VENDOR SERVLET: ENTERING showVendorDashboard ---");
        User sessionUser = getLoggedInUser(request); 

        if (sessionUser == null) {
            System.out.println("VendorServlet (showVendorDashboard): Session user is NULL. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        System.out.println("VendorServlet (showVendorDashboard): Session User: " + sessionUser.getName() + " (ID: " + sessionUser.getId() + "), Type: " + sessionUser.getUserType());

        if (!"VENDOR".equals(sessionUser.getUserType()) && !"ADMIN".equals(sessionUser.getUserType())) {
            System.out.println("VendorServlet (showVendorDashboard): User is not VENDOR or ADMIN. Redirecting to unauthorized.");
            response.sendRedirect(request.getContextPath() + "/unauthorized"); 
            return;
        }

        Vendor vendor = null;
        List<VendorService> services = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();

        try {
            // VENDOR specific logic
            if ("VENDOR".equals(sessionUser.getUserType())) {
                vendor = vendorDAO.findByUserId(sessionUser.getId());
                System.out.println("VendorServlet (showVendorDashboard): Fetched vendor profile for VENDOR user ID " + sessionUser.getId() + ". Vendor found: " + (vendor != null));
                if (vendor == null) {
                    System.out.println("VendorServlet (showVendorDashboard): VENDOR user ID " + sessionUser.getId() + " has no vendor profile. Redirecting to create profile.");
                    response.sendRedirect(request.getContextPath() + "/vendor/profile/create");
                    return; 
                }
            } 
            // ADMIN specific logic (an Admin might also be a Vendor)
            else if ("ADMIN".equals(sessionUser.getUserType())) {
                vendor = vendorDAO.findByUserId(sessionUser.getId()); // Check if admin has a personal vendor profile
                System.out.println("VendorServlet (showVendorDashboard): ADMIN user. Fetched vendor profile for user ID " + sessionUser.getId() + ". Vendor found: " + (vendor != null));
                // If admin is managing *all* vendors, that's a different page (e.g., /admin/vendors)
                // This dashboard is assumed to be for *their own* vendor profile if they have one
            }

            request.setAttribute("vendor", vendor); 

            if (vendor != null) {
                System.out.println("VendorServlet (showVendorDashboard): Vendor ID is " + vendor.getId());
                services = vendorServiceDAO.getServicesByVendorId(vendor.getId());
                System.out.println("VendorServlet (showVendorDashboard): Fetched " + (services != null ? services.size() : "0") + " services for vendor ID " + vendor.getId());
                bookings = bookingDAO.getBookingsByVendorId(vendor.getId()); // Fetch bookings for this vendor
                System.out.println("VendorServlet (showVendorDashboard): Fetched " + (bookings != null ? bookings.size() : "0") + " bookings for vendor ID " + vendor.getId());

            } else {
                System.out.println("VendorServlet (showVendorDashboard): Vendor object is null (e.g., Admin without personal vendor profile). Services and bookings will be empty for this view.");
            }
            request.setAttribute("services", services);
            request.setAttribute("bookings", bookings);

            // Handle success/error messages from session for redirects
            HttpSession session = request.getSession(false);
            if (session != null) {
                String successMessage = (String) session.getAttribute("successMessage");
                if (successMessage != null) {
                    request.setAttribute("successMessage", successMessage);
                    session.removeAttribute("successMessage");
                }
                String errorMessage = (String) session.getAttribute("errorMessage");
                if (errorMessage != null) {
                    request.setAttribute("errorMessage", errorMessage);
                    session.removeAttribute("errorMessage");
                }
            }


            System.out.println("VendorServlet (showVendorDashboard): Forwarding to /WEB-INF/views/vendor-dashboard.jsp");
            request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response);
            System.out.println("--- VENDOR SERVLET: EXITED showVendorDashboard (after forward) ---");

        } catch (SQLException sqle) {
            System.err.println("VendorServlet (showVendorDashboard): SQLException occurred!");
            sqle.printStackTrace(); // Print to server console
            request.setAttribute("errorMessage", "Database error on vendor dashboard: " + sqle.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("VendorServlet (showVendorDashboard): Generic Exception occurred!");
            e.printStackTrace(); // Print to server console
            request.setAttribute("errorMessage", "An unexpected error occurred loading vendor dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void listVendorServices(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET: ENTERING listVendorServices ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        if (vendor == null) {
            response.sendRedirect(request.getContextPath() + "/vendor/profile/create");
            return;
        }
        List<VendorService> services = vendorServiceDAO.getServicesByVendorId(vendor.getId());
        request.setAttribute("services", services);
        request.setAttribute("vendor", vendor); // For context in the JSP
        System.out.println("VendorServlet (listVendorServices): Forwarding to vendor-services.jsp (or dashboard if combined)");
        // This might be part of vendor-dashboard.jsp, adjust as needed
        request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response); 
    }

    private void showAddServiceForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET: ENTERING showAddServiceForm ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        if (vendor == null) {
             request.getSession().setAttribute("errorMessage", "Please create your vendor profile before adding services.");
            response.sendRedirect(request.getContextPath() + "/vendor/profile/create");
            return;
        }
        request.setAttribute("vendorService", new VendorService()); // New empty service for the form
        request.setAttribute("vendor", vendor);
        request.setAttribute("formAction", "add");
        System.out.println("VendorServlet (showAddServiceForm): Forwarding to vendor-service-form.jsp");
        request.getRequestDispatcher("/WEB-INF/views/vendor-service-form.jsp").forward(request, response);
    }

    private void showEditServiceForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, EventAggregatorException {
        System.out.println("--- VENDOR SERVLET: ENTERING showEditServiceForm ---");
        User sessionUser = getLoggedInUser(request);
         if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        if (vendor == null) {
            response.sendRedirect(request.getContextPath() + "/vendor/profile/create");
            return;
        }
        String serviceIdParam = request.getParameter("serviceId");
        if (serviceIdParam == null) {
            throw new EventAggregatorException("Service ID is required for editing.");
        }
        try {
            int serviceId = Integer.parseInt(serviceIdParam);
            VendorService service = vendorServiceDAO.getServiceById(serviceId);
            if (service == null || service.getVendorId() != vendor.getId()) {
                throw new EventAggregatorException("Service not found or you do not have permission to edit it.");
            }
            request.setAttribute("vendorService", service);
            request.setAttribute("vendor", vendor);
            request.setAttribute("formAction", "edit");
            System.out.println("VendorServlet (showEditServiceForm): Forwarding to vendor-service-form.jsp for editing service ID " + serviceId);
            request.getRequestDispatcher("/WEB-INF/views/vendor-service-form.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            throw new EventAggregatorException("Invalid Service ID format for editing.");
        }
    }

    private void saveVendorService(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, InvalidInputException {
        System.out.println("--- VENDOR SERVLET: ENTERING saveVendorService ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        if (vendor == null) {
            request.getSession().setAttribute("errorMessage", "Vendor profile not found. Cannot save service.");
            response.sendRedirect(request.getContextPath() + "/vendor/profile/create");
            return;
        }

        String serviceIdParam = request.getParameter("id"); // hidden field for edit
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String serviceType = request.getParameter("serviceType"); // This could be the vendor's main type or a sub-type
        String priceStr = request.getParameter("price");
        String location = request.getParameter("location"); // Often same as vendor location
        String imageUrl = request.getParameter("imageUrl");

        // Validation
        if (name == null || name.trim().isEmpty() ||
            serviceType == null || serviceType.trim().isEmpty() ||
            priceStr == null || priceStr.trim().isEmpty()) {
            throw new InvalidInputException("Service Name, Type, and Price are required.");
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
             if (price < 0) throw new InvalidInputException("Price cannot be negative.");
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid price format.");
        }

        VendorService service;
        boolean isNewService = (serviceIdParam == null || serviceIdParam.trim().isEmpty());

        if (isNewService) {
            service = new VendorService();
            service.setVendorId(vendor.getId());
            System.out.println("VendorServlet (saveVendorService): Adding new service for vendor ID " + vendor.getId());
        } else {
            try {
                int serviceId = Integer.parseInt(serviceIdParam);
                service = vendorServiceDAO.getServiceById(serviceId);
                if (service == null || service.getVendorId() != vendor.getId()) {
                    // Security check or error
                    request.getSession().setAttribute("errorMessage", "Service not found or unauthorized to edit.");
                    response.sendRedirect(request.getContextPath() + "/vendor-dashboard");
                    return;
                }
                System.out.println("VendorServlet (saveVendorService): Updating service ID " + serviceId);
            } catch (NumberFormatException e) {
                 request.getSession().setAttribute("errorMessage", "Invalid service ID for update.");
                 response.sendRedirect(request.getContextPath() + "/vendor-dashboard");
                 return;
            }
        }

        service.setName(name);
        service.setDescription(description);
        service.setServiceType(serviceType); // Consider if this should be fixed to vendor's type or flexible
        service.setPrice(price);
        service.setLocation(location != null ? location : vendor.getLocation()); // Default to vendor location
        service.setImageUrl(imageUrl);
        service.setActive(true); // Default to active
        service.setCreatedAt(new java.sql.Timestamp(new Date().getTime())); // Set for new, or update for existing
        service.setUpdatedAt(new java.sql.Timestamp(new Date().getTime()));


        boolean success;
        if (isNewService) {
            success = vendorServiceDAO.addService(service);
        } else {
            success = vendorServiceDAO.updateService(service);
        }

        if (success) {
            request.getSession().setAttribute("successMessage", "Service " + (isNewService ? "added" : "updated") + " successfully!");
            System.out.println("VendorServlet (saveVendorService): Service saved successfully. Redirecting to dashboard.");
            response.sendRedirect(request.getContextPath() + "/vendor-dashboard");
        } else {
            request.setAttribute("errorMessage", "Failed to save service. Please try again.");
            request.setAttribute("vendorService", service);
            request.setAttribute("vendor", vendor);
            request.setAttribute("formAction", isNewService ? "add" : "edit");
            System.err.println("VendorServlet (saveVendorService): Failed to save service.");
            request.getRequestDispatcher("/WEB-INF/views/vendor-service-form.jsp").forward(request, response);
        }
    }

    private void deleteVendorService(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET: ENTERING deleteVendorService ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor vendor = vendorDAO.findByUserId(sessionUser.getId());
        if (vendor == null) {
             response.sendRedirect(request.getContextPath() + "/vendor-dashboard"); // Should not happen if UI is correct
            return;
        }

        String serviceIdParam = request.getParameter("serviceId");
        try {
            int serviceId = Integer.parseInt(serviceIdParam);
            VendorService service = vendorServiceDAO.getServiceById(serviceId);
            // Ensure the service belongs to the logged-in vendor
            if (service != null && service.getVendorId() == vendor.getId()) {
                boolean deleted = vendorServiceDAO.deleteService(serviceId);
                if (deleted) {
                    request.getSession().setAttribute("successMessage", "Service deleted successfully.");
                    System.out.println("VendorServlet (deleteVendorService): Service ID " + serviceId + " deleted.");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to delete service.");
                    System.err.println("VendorServlet (deleteVendorService): Failed to delete service ID " + serviceId + " from DAO.");
                }
            } else {
                request.getSession().setAttribute("errorMessage", "Service not found or you do not have permission to delete it.");
                 System.err.println("VendorServlet (deleteVendorService): Attempt to delete unauthorized/non-existent service ID " + serviceId);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid service ID for deletion.");
            System.err.println("VendorServlet (deleteVendorService): Invalid service ID format: " + serviceIdParam);
        }
        response.sendRedirect(request.getContextPath() + "/vendor-dashboard");
    }

    // ADMIN Functionality
    private void listAllVendorsForAdmin(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET (Admin): ENTERING listAllVendorsForAdmin ---");
        List<AdminVendorView> adminVendorViews = vendorDAO.findAllForAdminView();
        request.setAttribute("adminVendorViews", adminVendorViews);
        System.out.println("VendorServlet (Admin - listAllVendorsForAdmin): Found " + adminVendorViews.size() + " vendors for admin. Forwarding to admin-vendors.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-vendors.jsp").forward(request, response);
    }

    private void approveVendor(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET (Admin): ENTERING approveVendor ---");
        String vendorIdParam = request.getParameter("vendorId");
        try {
            int vendorId = Integer.parseInt(vendorIdParam);
            boolean success = vendorDAO.updateVendorStatus(vendorId, true); // true for active
            if (success) {
                request.getSession().setAttribute("successMessage", "Vendor ID " + vendorId + " approved successfully.");
                 System.out.println("VendorServlet (Admin - approveVendor): Vendor ID " + vendorId + " approved.");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to approve vendor ID " + vendorId + ".");
                System.err.println("VendorServlet (Admin - approveVendor): Failed to approve vendor ID " + vendorId + " via DAO.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid vendor ID for approval.");
            System.err.println("VendorServlet (Admin - approveVendor): Invalid vendor ID format: " + vendorIdParam);
        }
        response.sendRedirect(request.getContextPath() + "/admin/vendors");
    }

    private void rejectVendor(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        // For rejection, you might want to "deactivate" or have a "rejected" status.
        // For simplicity, deactivating (setting isActive = false).
        // Or you might choose to delete the vendor record. Deleting is cleaner if they can't reapply with same info.
        // Here, let's just deactivate.
        System.out.println("--- VENDOR SERVLET (Admin): ENTERING rejectVendor ---");
        String vendorIdParam = request.getParameter("vendorId");
         try {
            int vendorId = Integer.parseInt(vendorIdParam);
            boolean success = vendorDAO.updateVendorStatus(vendorId, false); // false for inactive
            if (success) {
                request.getSession().setAttribute("successMessage", "Vendor ID " + vendorId + " rejected (deactivated) successfully.");
                 System.out.println("VendorServlet (Admin - rejectVendor): Vendor ID " + vendorId + " rejected/deactivated.");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to reject vendor ID " + vendorId + ".");
                System.err.println("VendorServlet (Admin - rejectVendor): Failed to reject vendor ID " + vendorId + " via DAO.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid vendor ID for rejection.");
             System.err.println("VendorServlet (Admin - rejectVendor): Invalid vendor ID format: " + vendorIdParam);
        }
        response.sendRedirect(request.getContextPath() + "/admin/vendors");
    }
}