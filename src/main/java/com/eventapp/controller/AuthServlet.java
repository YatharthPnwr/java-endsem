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
 * Servlet for handling user authentication (login, registration, logout).
 */
@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    
    public AuthServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/logout".equals(path)) {
            handleLogout(request, response);
        } else if ("/login".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else if ("/register".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/login".equals(path)) {
            handleLogin(request, response);
        } else if ("/register".equals(path)) {
            handleRegistration(request, response);
        }
    }
    
    /**
     * Handles user login.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            // Validate input
            userService.validateLoginInput(email, password);
            
            // Authenticate user
            User user = userService.authenticateUser(email, password);
            
            if (user != null) {
                // Create session and add user to it
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userLoggedIn", true);
                session.setAttribute("userName", user.getName());
                session.setAttribute("userType", user.getUserType());
                
                // Redirect to appropriate page based on user type
                String redirectPath = determineRedirectPath(user.getUserType());
                response.sendRedirect(request.getContextPath() + redirectPath);
            } else {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } catch (InvalidInputException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles user registration.
     */
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String userType = request.getParameter("userType") != null ? request.getParameter("userType") : "CUSTOMER";
        
        try {
            // Validate registration input
            userService.validateRegistrationInput(name, email, phone, password, confirmPassword);
            
            // Check if email already exists
            if (userService.isEmailExists(email)) {
                request.setAttribute("errorMessage", "Email is already registered.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }
            
            // Create user object
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            user.setUserType(userType);
            
            // Register user
            int userId = userService.registerUser(user);
            
            if (userId > 0) {
                user.setId(userId);
                
                // Create session and add user to it
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userLoggedIn", true);
                session.setAttribute("userName", user.getName());
                session.setAttribute("userType", user.getUserType());
                
                request.setAttribute("successMessage", "Registration successful! Welcome to Event Aggregator.");
                
                // Redirect to appropriate page based on user type
                String redirectPath = determineRedirectPath(user.getUserType());
                response.sendRedirect(request.getContextPath() + redirectPath);
            } else {
                request.setAttribute("errorMessage", "Registration failed. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            }
        } catch (InvalidInputException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles user logout.
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
    
    /**
     * Determines the redirect path based on user type.
     */
    private String determineRedirectPath(String userType) {
        if ("ADMIN".equals(userType)) {
            return "/admin/dashboard";
        } else if ("VENDOR".equals(userType)) {
            return "/vendor-dashboard";
        } else {
            return "/vendors"; // Default for CUSTOMER
        }
    }
}