<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Details Test</title>
</head>
<body>
    <h1>Booking Details</h1>
    <p>This is a simplified test page.</p>
    
    <p>Booking ID: ${booking.id}</p>
    <p>Event Date: ${booking.eventDate}</p>
    <p>Location: ${booking.eventLocation}</p>
    <p>Status: ${booking.status}</p>
    <p>Total Cost: ₹${booking.totalCost}</p>
    
    <h2>Vendors:</h2>
    <ul>
    <% 
    java.util.List<com.eventapp.model.Vendor> vendors = 
        (java.util.List<com.eventapp.model.Vendor>)request.getAttribute("bookingVendors");
    if(vendors != null) {
        for(com.eventapp.model.Vendor vendor : vendors) {
            out.println("<li>" + vendor.getName() + "</li>");
        }
    } else {
        out.println("<li>No vendors found</li>");
    }
    %>
    </ul>
</body>
</html>