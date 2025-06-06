package com.eventapp.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for handling access denied scenarios.
 */
@WebServlet("/accessDenied")
public class AccessDeniedServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * Handles GET requests for access denied page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/access-denied.jsp").forward(request, response);
    }
}