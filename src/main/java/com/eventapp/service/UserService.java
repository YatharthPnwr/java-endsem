package com.eventapp.service;

import com.eventapp.dao.UserDAO;
import com.eventapp.exception.InvalidInputException;
import com.eventapp.model.User;

/**
 * Service class for user-related business logic.
 */
public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Authenticate a user with email and password.
     * 
     * @param email User's email
     * @param password User's password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        return userDAO.findByEmailAndPassword(email, password);
    }
    
    /**
     * Register a new user.
     * 
     * @param user User object with registration data
     * @return User ID if registration successful, -1 otherwise
     * @throws InvalidInputException if email already exists
     */
    public int registerUser(User user) throws InvalidInputException {
        if (userDAO.isEmailExists(user.getEmail())) {
            throw new InvalidInputException("Email already exists");
        }
        
        return userDAO.registerUser(user);
    }
    
    /**
     * Check if email is already registered.
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        return userDAO.isEmailExists(email);
    }
    
    /**
     * Get user by ID.
     * 
     * @param userId User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }
    
    /**
     * Update user profile information.
     * 
     * @param user Updated User object
     * @param currentEmail Current email before update
     * @return true if update successful, false otherwise
     * @throws InvalidInputException if email is already in use by another user
     */
    public boolean updateUser(User user, String currentEmail) throws InvalidInputException {
        // If email has changed, check if new email already exists
        if (!user.getEmail().equals(currentEmail) && userDAO.isEmailExists(user.getEmail())) {
            throw new InvalidInputException("Email is already in use by another account");
        }
        
        return userDAO.updateUser(user);
    }
    
    /**
     * Update user password.
     * 
     * @param userId User ID
     * @param currentPassword Current password for verification
     * @param newPassword New password
     * @param confirmPassword Confirm new password
     * @return true if password update successful, false otherwise
     * @throws InvalidInputException if current password is incorrect or passwords don't match
     */
    public boolean updatePassword(int userId, String currentPassword, String newPassword, String confirmPassword) 
            throws InvalidInputException {
        // Get user to verify current password
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // Verify current password (authenticate)
        User authenticated = userDAO.findByEmailAndPassword(user.getEmail(), currentPassword);
        if (authenticated == null) {
            throw new InvalidInputException("Current password is incorrect");
        }
        
        // Validate new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new InvalidInputException("New password cannot be empty");
        }
        
        if (newPassword.length() < 6) {
            throw new InvalidInputException("New password must be at least 6 characters long");
        }
        
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidInputException("New passwords do not match");
        }
        
        // Update password
        return userDAO.updatePassword(userId, newPassword);
    }
    
    /**
     * Validate user input for registration.
     * 
     * @param name User name
     * @param email User email
     * @param phone User phone
     * @param password User password
     * @param confirmPassword Password confirmation
     * @throws InvalidInputException if any validation fails
     */
    public void validateRegistrationInput(String name, String email, String phone, String password, String confirmPassword) 
            throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Name is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email is required.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidInputException("Invalid email format.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidInputException("Phone number is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password is required.");
        }
        if (password.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new InvalidInputException("Passwords do not match.");
        }
    }
    
    /**
     * Validate user input for profile updates.
     * 
     * @param name User name
     * @param email User email
     * @param phone User phone
     * @throws InvalidInputException if any validation fails
     */
    public void validateProfileUpdateInput(String name, String email, String phone) 
            throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email is required");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidInputException("Invalid email format");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidInputException("Phone number is required");
        }
    }
    
    /**
     * Validate user input for login.
     * 
     * @param email User email
     * @param password User password
     * @throws InvalidInputException if any validation fails
     */
    public void validateLoginInput(String email, String password) throws InvalidInputException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password is required.");
        }
    }
}