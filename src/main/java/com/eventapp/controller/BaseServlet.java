package com.eventapp.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventapp.model.User;

/**
 * Base servlet that provides common functionality for all servlets in the application.
 */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * Helper method to check if a user is logged in.
     * 
     * @param request The HTTP request
     * @return true if the user is logged in
     */
    protected boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null && session.getAttribute("user") != null);
    }
    
    /**
     * Helper method to get the logged-in user.
     * 
     * @param request The HTTP request
     * @return The logged-in user or null if not logged in
     */
    protected User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
    /**
     * Helper method to verify if the logged-in user is an admin.
     * 
     * @param request The HTTP request
     * @return true if the user is logged in and is an admin
     */
    protected boolean isAdmin(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return (user != null && "ADMIN".equals(user.getUserType()));
    }
    
    /**
     * Helper method to verify if the logged-in user is a vendor.
     * 
     * @param request The HTTP request
     * @return true if the user is logged in and is a vendor
     */
    protected boolean isVendor(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return (user != null && "VENDOR".equals(user.getUserType()));
    }
    
    /**
     * Helper method to redirect to login page if not logged in.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return true if the user is logged in, false otherwise
     * @throws IOException If an I/O error occurs
     * @throws ServletException If a servlet error occurs
     */
    protected boolean checkLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        if (!isLoggedIn(request)) {
            request.setAttribute("message", "Please log in to access this page.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return false;
        }
        return true;
    }
    
    /**
     * Helper method to redirect to appropriate page if user doesn't have admin access.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return true if the user is an admin, false otherwise
     * @throws IOException If an I/O error occurs
     * @throws ServletException If a servlet error occurs
     */
    protected boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/unauthorized");
            return false;
        }
        return true;
    }
    
    /**
     * Helper method to redirect to appropriate page if user doesn't have vendor access.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return true if the user is a vendor, false otherwise
     * @throws IOException If an I/O error occurs
     * @throws ServletException If a servlet error occurs
     */
    protected boolean checkVendorAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        if (!isVendor(request)) {
            response.sendRedirect(request.getContextPath() + "/unauthorized");
            return false;
        }
        return true;
    }
}
