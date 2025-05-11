package com.eventapp.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter to handle authentication and authorization.
 * Protects pages that should only be accessible to logged-in users.
 */
@WebFilter(urlPatterns = {"/admin/*", "/vendor/*", "/checkout", "/cart"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Get current session (don't create a new one if it doesn't exist)
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        // Check if user is logged in
        if (!isLoggedIn) {
            // User is not logged in, redirect to login page
            httpRequest.setAttribute("errorMessage", "You must login to access this resource");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // For admin-specific pages
        String requestURI = httpRequest.getRequestURI();
        String userType = (String) session.getAttribute("userType");
        
        if (requestURI.contains("/admin/") && !"ADMIN".equals(userType)) {
            // User is not an admin, redirect to access denied page or home page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/accessDenied");
            return;
        }
        
        // For vendor-specific pages
        if (requestURI.contains("/vendor/") && !"VENDOR".equals(userType) && !"ADMIN".equals(userType)) {
            // User is not a vendor or admin, redirect to access denied page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/accessDenied");
            return;
        }
        
        // User is authenticated and authorized, proceed with the request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}