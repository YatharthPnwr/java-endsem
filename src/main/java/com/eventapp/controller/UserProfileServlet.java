package com.eventapp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventapp.exception.InvalidInputException;
import com.eventapp.model.User;
import com.eventapp.service.UserService;

/**
 * Servlet for handling user profile operations (view and update).
 */
@WebServlet(urlPatterns = {"/user/profile"})
public class UserProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    
    public UserProfileServlet() {
        this.userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Simply display the profile page with current user information
        User user = getLoggedInUser(request);
        
        if (user == null) {
            // Redirect to login if not logged in
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        User user = getLoggedInUser(request);
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("updateProfile".equals(action)) {
            handleProfileUpdate(request, response, user);
        } else if ("updatePassword".equals(action)) {
            handlePasswordUpdate(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/user/profile");
        }
    }
    
    /**
     * Handles updating profile information (name, email, phone).
     */
    private void handleProfileUpdate(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String currentEmail = currentUser.getEmail(); // Store current email before update
        
        try {
            // Validate input
            userService.validateProfileUpdateInput(name, email, phone);
            
            // Update user object
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            
            // Update in database
            boolean updated = userService.updateUser(currentUser, currentEmail);
            
            if (updated) {
                // Update session user info
                HttpSession session = request.getSession();
                session.setAttribute("user", currentUser);
                session.setAttribute("userName", currentUser.getName());
                
                request.setAttribute("successMessage", "Profile updated successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to update profile. Please try again.");
            }
        } catch (InvalidInputException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        // Reload user in case of any errors
        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
    }
    
    /**
     * Handles updating password.
     */
    private void handlePasswordUpdate(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        try {
            boolean updated = userService.updatePassword(
                    currentUser.getId(), currentPassword, newPassword, confirmPassword);
            
            if (updated) {
                request.setAttribute("successMessage", "Password updated successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to update password. Please try again.");
            }
        } catch (InvalidInputException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/WEB-INF/views/user-profile.jsp").forward(request, response);
    }
}