package com.eventapp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventapp.dao.CartDAO;
import com.eventapp.dao.VendorDAO; // To fetch vendor details for cart display
import com.eventapp.model.Cart;
import com.eventapp.model.User;
import com.eventapp.model.Vendor;

@WebServlet(urlPatterns = {"/cart", "/add-to-cart", "/remove-from-cart", "/clear-cart"})
public class CartServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private CartDAO cartDAO;
    private VendorDAO vendorDAO;

    public CartServlet() {
        super();
        this.cartDAO = new CartDAO();
        this.vendorDAO = new VendorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("CartServlet - doGet: Path = " + path);

        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + path); // Redirect to login
            return;
        }

        if ("/cart".equals(path)) {
            showCart(request, response, sessionUser);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("CartServlet - doPost: Path = " + path);

        User sessionUser = getLoggedInUser(request);
        if (sessionUser == null) {
            // For POST actions, it's usually better to return an error or redirect to login with a message
            // rather than trying to process the action.
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            switch (path) {
                case "/add-to-cart":
                    addToCart(request, response, sessionUser);
                    break;
                case "/remove-from-cart":
                    removeFromCart(request, response, sessionUser);
                    break;
                case "/clear-cart":
                    clearCart(request, response, sessionUser);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            System.err.println("CartServlet - doPost: NumberFormatException - " + e.getMessage());
            request.setAttribute("errorMessage", "Invalid ID format for cart operation.");
            showCart(request, response, sessionUser); // Show cart with error
        } catch (SQLException e) {
            System.err.println("CartServlet - doPost: SQLException - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error processing cart operation.");
            showCart(request, response, sessionUser); // Show cart with error
        }
    }

    private void showCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("CartServlet: showCart for user ID " + user.getId());
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getUserId() != user.getId()) {
            // Load cart from DB if not in session or if user changed
            cart = new Cart(user.getId());
            try {
                List<Integer> vendorIdsFromDB = cartDAO.getCartItems(user.getId());
                double totalCost = 0;
                for (Integer vendorId : vendorIdsFromDB) {
                    Vendor vendor = vendorDAO.findById(vendorId); // Fetch vendor to get cost
                    if (vendor != null) {
                        cart.addVendor(vendorId, vendor.getBaseCost()); // Add to cart model
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error loading cart from DB for user " + user.getId() + ": " + e.getMessage());
                request.setAttribute("errorMessage", "Could not load your cart from the database.");
                // Initialize an empty cart to prevent NPEs in JSP
            }
            session.setAttribute("cart", cart);
        }
        
        // Fetch vendor details for display
        List<Vendor> cartVendors = new ArrayList<>();
        if (cart.getVendorIds() != null && !cart.getVendorIds().isEmpty()) {
            try {
                for (Integer vendorId : cart.getVendorIds()) {
                    Vendor vendor = vendorDAO.findById(vendorId);
                    if (vendor != null) {
                        cartVendors.add(vendor);
                    }
                }
            } catch (SQLException e) {
                 System.err.println("Error fetching vendor details for cart: " + e.getMessage());
                 request.setAttribute("errorMessage", "Could not load vendor details for your cart.");
            }
        }
        request.setAttribute("cartVendors", cartVendors);
        System.out.println("CartServlet: Forwarding to cart.jsp. Items in cart: " + cart.getItemCount());
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException, SQLException {
        String vendorIdParam = request.getParameter("vendorId");
        int vendorId = Integer.parseInt(vendorIdParam); // Add try-catch for NumberFormatException
        System.out.println("CartServlet: addToCart - User ID: " + user.getId() + ", Vendor ID: " + vendorId);

        boolean success = cartDAO.addToCart(user.getId(), vendorId);
        HttpSession session = request.getSession();

        if (success) {
            // Update session cart
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null || cart.getUserId() != user.getId()) {
                cart = new Cart(user.getId());
            }
            Vendor vendor = vendorDAO.findById(vendorId); // Fetch vendor to get cost
            if (vendor != null && !cart.getVendorIds().contains(vendorId)) { // Ensure not already added in session model
                cart.addVendor(vendorId, vendor.getBaseCost());
            }
            session.setAttribute("cart", cart);
            session.setAttribute("successMessage", "Service added to cart!");
        } else {
            session.setAttribute("errorMessage", "Failed to add service to cart.");
        }
        // Redirect back to the page where "add to cart" was clicked, or to vendors page.
        // Using referer is common, but can be null. Fallback to /vendors or /cart.
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/vendors");
        }
    }

    private void removeFromCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException, SQLException {
        String vendorIdParam = request.getParameter("vendorId");
        int vendorId = Integer.parseInt(vendorIdParam);
        System.out.println("CartServlet: removeFromCart - User ID: " + user.getId() + ", Vendor ID: " + vendorId);

        boolean success = cartDAO.removeFromCart(user.getId(), vendorId);
        HttpSession session = request.getSession();

        if (success) {
            // Update session cart
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null && cart.getUserId() == user.getId()) {
                 Vendor vendor = vendorDAO.findById(vendorId); // Fetch vendor to get cost
                 if (vendor != null) {
                    cart.removeVendor(vendorId, vendor.getBaseCost());
                 }
            }
            session.setAttribute("cart", cart);
            session.setAttribute("successMessage", "Service removed from cart.");
        } else {
            session.setAttribute("errorMessage", "Failed to remove service from cart.");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException, SQLException {
        System.out.println("CartServlet: clearCart for user ID " + user.getId());
        boolean success = cartDAO.clearCart(user.getId());
        HttpSession session = request.getSession();
        if (success) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null) {
                cart.clear();
            }
            session.setAttribute("cart", cart); // or session.removeAttribute("cart");
            session.setAttribute("successMessage", "Cart cleared successfully.");
        } else {
            session.setAttribute("errorMessage", "Failed to clear cart.");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}