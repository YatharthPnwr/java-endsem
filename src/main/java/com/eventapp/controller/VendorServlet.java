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
    private BookingDAO bookingDAO; 
    private UserDAO userDAO;      
    private UserService userService;

    public VendorServlet() {
        super();
        this.vendorDAO = new VendorDAO();
        this.vendorServiceDAO = new VendorServiceDAO();
        this.bookingDAO = new BookingDAO(); 
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
                case "/vendor/details": // Changed this to match JSP link if vendor ID is part of path
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
                case "/vendor/services": 
                    listVendorServices(request, response);
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
                    // Check if it's a vendor detail request like /vendor/123
                    if (path.startsWith("/vendor/") && path.matches("/vendor/\\d+")) {
                        showVendorDetails(request, response); // Re-route to showVendorDetails
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
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
                    deleteVendorService(request, response); 
                    break;
                case "/admin/vendor/approve":
                     if (isAdmin(request)) {
                        approveVendor(request, response); 
                    } else {
                        response.sendRedirect(request.getContextPath() + "/unauthorized");
                    }
                    break;
                case "/admin/vendor/reject":
                     if (isAdmin(request)) {
                        rejectVendor(request, response); 
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
            // Try to reshow form with submitted data if applicable
            reShowFormOnError(request, response, path, e.getMessage());
        } catch (InvalidInputException e) {
            System.err.println("VendorServlet doPost - InvalidInputException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
            reShowFormOnError(request, response, path, e.getMessage());
        } catch (Exception e) { 
            System.err.println("VendorServlet doPost - Generic Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void reShowFormOnError(HttpServletRequest request, HttpServletResponse response, String path, String errorMessage) 
        throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage); // Ensure error message is set
        if (path.equals("/vendor/profile/save")) {
            User loggedInUser = getLoggedInUser(request);
            Vendor vendorToRepopulate = new Vendor(); 
            // Repopulate from request parameters
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
                // Repopulate service from request
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
                 System.err.println("VendorServlet reShowFormOnError (service save) - SQLException: " + sqlEx.getMessage());
                 // Even if DB error, try to show form with what we have
            }
            request.setAttribute("vendorService", serviceToRepopulate);
            request.setAttribute("vendor", currentVendor); // vendor might be null if DB error above
            request.setAttribute("formAction", (serviceToRepopulate.getId() > 0) ? "edit" : "add");
            request.getRequestDispatcher("/WEB-INF/views/vendor-service-form.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }


    private void listVendors(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("VendorServlet: listVendors called");
        String typeFilter = request.getParameter("type");
        String queryFilter = request.getParameter("query"); // Added search query
        String sortParam = request.getParameter("sort"); // Get sort parameter

        List<Vendor> vendors;

        // Basic logic: if typeFilter is present, filter by type, else get all.
        // In a more complex app, you'd have a DAO method that takes all filters.
        // For now, just handling type and sort.
        if (typeFilter != null && !typeFilter.trim().isEmpty()) {
            vendors = vendorDAO.findByType(typeFilter, sortParam); // Pass sortParam
            request.setAttribute("selectedType", typeFilter);
             System.out.println("VendorServlet: Filtering by type: " + typeFilter + ", Sort: " + sortParam + ", Found " + vendors.size() + " vendors.");
        } else {
            vendors = vendorDAO.findAllActive(sortParam); // Pass sortParam
            System.out.println("VendorServlet: Fetching all active vendors. Sort: " + sortParam + ", Found " + vendors.size() + " vendors.");
        }
        // TODO: Add queryFilter and price filters to DAO calls if needed

        request.setAttribute("vendors", vendors);
        List<String> vendorTypes = vendorDAO.findDistinctVendorTypes();
        request.setAttribute("vendorTypes", vendorTypes);
        request.setAttribute("currentSort", sortParam); // For JSP to highlight active sort

        System.out.println("VendorServlet: Forwarding to vendors.jsp");
        request.getRequestDispatcher("/WEB-INF/views/vendors.jsp").forward(request, response);
    }


    private void showVendorDetails(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, EventAggregatorException {
        System.out.println("VendorServlet: showVendorDetails called");
        String idParam = request.getParameter("id"); // Standard way to get ID from query
        String pathInfo = request.getPathInfo(); // For /vendor/123 style URLs

        if (idParam == null && pathInfo != null && pathInfo.length() > 1) {
            try {
                idParam = pathInfo.substring(1); // Get ID from path like /123
            } catch (Exception e) {
                // ignore, idParam remains null
            }
        }
        
        if (idParam == null) {
            throw new EventAggregatorException("Vendor ID is required.");
        }
        try {
            int vendorId = Integer.parseInt(idParam);
            Vendor vendor = vendorDAO.findById(vendorId);
            if (vendor == null || !vendor.isActive()) { 
                 System.out.println("VendorServlet: Vendor not found or inactive for ID: " + vendorId);
                request.setAttribute("errorMessage", "Vendor not found or is not currently active.");
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
                return;
            }
            List<VendorService> services = vendorServiceDAO.getServicesByVendorId(vendorId);
            request.setAttribute("vendor", vendor);
            request.setAttribute("services", services); // Assuming services are still relevant on detail page
            System.out.println("VendorServlet: Showing details for vendor ID " + vendorId + ". Forwarding to vendor-details.jsp");
            request.getRequestDispatcher("/WEB-INF/views/vendor-details.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            throw new EventAggregatorException("Invalid Vendor ID format: " + idParam);
        }
    }

    // ... (other methods like showCreateVendorProfileForm, saveVendorProfile etc. remain as they were)
    // Ensure they are using the correct DAOs and error handling.

    private void showCreateVendorProfileForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET: ENTERING showCreateVendorProfileForm ---");
        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null || !"VENDOR".equals(sessionUser.getUserType())) {
            System.out.println("VendorServlet (showCreateVendorProfileForm): User not VENDOR or not logged in. Redirecting.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Vendor existingVendor = vendorDAO.findByUserId(sessionUser.getId());
        if (existingVendor != null) {
            System.out.println("VendorServlet (showCreateVendorProfileForm): Vendor profile already exists for user ID " + sessionUser.getId() + ". Redirecting to edit.");
            response.sendRedirect(request.getContextPath() + "/vendor/profile/edit");
            return;
        }
        request.setAttribute("vendor", new Vendor()); 
        request.setAttribute("user", sessionUser); 
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
        String name = request.getParameter("name"); // Corresponds to business_name in DB
        String type = request.getParameter("type"); // Corresponds to service_type in DB
        String description = request.getParameter("description");
        // contactInfo is derived, contact_email and contact_phone are for DB
        String location = request.getParameter("location");
        String baseCostStr = request.getParameter("baseCost");
        String imageUrl = request.getParameter("imageUrl");

        if (name == null || name.trim().isEmpty() ||
            type == null || type.trim().isEmpty() ||
            location == null || location.trim().isEmpty() ||
            baseCostStr == null || baseCostStr.trim().isEmpty()) {
            throw new InvalidInputException("Business Name, Service Type, Location, and Base Cost are required fields.");
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
            vendor.setActive(false); 
            System.out.println("VendorServlet (saveVendorProfile): Creating new vendor profile for user ID " + sessionUser.getId());
        } else {
            System.out.println("VendorServlet (saveVendorProfile): Updating existing vendor profile ID " + vendor.getId());
        }

        vendor.setName(name); // This is business_name
        vendor.setType(type);   // This is service_type
        vendor.setDescription(description);
        // Contact info for the Vendor object can be composite, but for DB it's separate
        vendor.setContactInfo(sessionUser.getEmail() + (sessionUser.getPhone() != null && !sessionUser.getPhone().isEmpty() ? ", " + sessionUser.getPhone() : ""));
        // The DAO's add/update methods will handle splitting this or using user's email/phone for DB
        vendor.setLocation(location);
        vendor.setBaseCost(baseCost);
        vendor.setImageUrl(imageUrl); 
        
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
            // Re-throw specific exception or set error attribute and forward
            throw new SQLException("Failed to save vendor profile. Please try again.");
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
            vendor = vendorDAO.findByUserId(sessionUser.getId()); 
            System.out.println("VendorServlet (showVendorDashboard): ADMIN user. Fetched vendor profile for user ID " + sessionUser.getId() + ". Vendor found: " + (vendor != null));
        }

        request.setAttribute("vendor", vendor); 

        if (vendor != null) {
            System.out.println("VendorServlet (showVendorDashboard): Vendor ID is " + vendor.getId());
            services = vendorServiceDAO.getServicesByVendorId(vendor.getId());
            System.out.println("VendorServlet (showVendorDashboard): Fetched " + (services != null ? services.size() : "0") + " services for vendor ID " + vendor.getId());
            bookings = bookingDAO.getBookingsByVendorId(vendor.getId()); 
            System.out.println("VendorServlet (showVendorDashboard): Fetched " + (bookings != null ? bookings.size() : "0") + " bookings for vendor ID " + vendor.getId());
        } else {
            System.out.println("VendorServlet (showVendorDashboard): Vendor object is null (e.g., Admin without personal vendor profile). Services and bookings will be empty for this view.");
        }
        request.setAttribute("services", services);
        request.setAttribute("bookings", bookings);

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
        request.setAttribute("vendor", vendor); 
        System.out.println("VendorServlet (listVendorServices): Forwarding to vendor-dashboard.jsp (as services are part of it)");
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
        request.setAttribute("vendorService", new VendorService()); 
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

        String serviceIdParam = request.getParameter("id"); 
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String serviceType = request.getParameter("serviceType"); 
        String priceStr = request.getParameter("price");
        String location = request.getParameter("location"); 
        String imageUrl = request.getParameter("imageUrl");

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
        service.setServiceType(serviceType); 
        service.setPrice(price);
        service.setLocation(location != null && !location.trim().isEmpty() ? location : vendor.getLocation()); 
        service.setImageUrl(imageUrl);
        service.setActive(true); 
        // Timestamps are handled by DB (DEFAULT CURRENT_TIMESTAMP and ON UPDATE CURRENT_TIMESTAMP)
        // or set explicitly if needed:
        // if (isNewService) service.setCreatedAt(new java.sql.Timestamp(new Date().getTime()));
        // service.setUpdatedAt(new java.sql.Timestamp(new Date().getTime()));

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
            // Re-throw specific exception or set error attribute and forward
            throw new SQLException("Failed to save service. Please try again.");
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
             response.sendRedirect(request.getContextPath() + "/vendor-dashboard"); 
            return;
        }

        String serviceIdParam = request.getParameter("serviceId");
        try {
            int serviceId = Integer.parseInt(serviceIdParam);
            VendorService service = vendorServiceDAO.getServiceById(serviceId);
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

    private void listAllVendorsForAdmin(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET (Admin): ENTERING listAllVendorsForAdmin ---");
        List<AdminVendorView> adminVendorViews = vendorDAO.findAllForAdminView();
        request.setAttribute("adminVendorViews", adminVendorViews);
        System.out.println("VendorServlet (Admin - listAllVendorsForAdmin): Found " + adminVendorViews.size() + " vendors for admin. Forwarding to admin/admin-vendors.jsp");
        // Corrected path for admin views
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-vendors.jsp").forward(request, response);
    }

    private void approveVendor(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException {
        System.out.println("--- VENDOR SERVLET (Admin): ENTERING approveVendor ---");
        String vendorIdParam = request.getParameter("vendorId");
        try {
            int vendorId = Integer.parseInt(vendorIdParam);
            boolean success = vendorDAO.updateVendorStatus(vendorId, true); 
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
        System.out.println("--- VENDOR SERVLET (Admin): ENTERING rejectVendor ---");
        String vendorIdParam = request.getParameter("vendorId");
         try {
            int vendorId = Integer.parseInt(vendorIdParam);
            boolean success = vendorDAO.updateVendorStatus(vendorId, false); 
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