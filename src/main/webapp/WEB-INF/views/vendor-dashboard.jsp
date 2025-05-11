<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Vendor Dashboard" />
</jsp:include>

<div class="container py-5">
    <h1>Vendor Dashboard Test Page</h1>
    <p>If you see this, the servlet forwarded correctly to vendor-dashboard.jsp.</p>
    
    <hr/>
    <h3>Session Information:</h3>
    <p>User from session: ${sessionScope.user.name} (Type: ${sessionScope.user.userType}, ID: ${sessionScope.user.id})</p>

    <hr/>
    <h3>Vendor Profile Information (from request attribute 'vendor'):</h3>
    <c:choose>
        <c:when test="${not empty vendor}">
            <p>Vendor Name: ${vendor.name}</p>
            <p>Vendor ID: ${vendor.id}</p>
            <p>Vendor User ID (links to User table): ${vendor.userId}</p>
            <p>Vendor Type: ${vendor.type}</p>
            <p>Description: ${vendor.description}</p>
            <p>Contact: ${vendor.contactInfo}</p>
            <p>Location: ${vendor.location}</p>
            <p>Base Cost: <fmt:formatNumber value="${vendor.baseCost}" type="currency" currencySymbol="₹" /></p>
            <p>Active: ${vendor.active}</p>
            <p>Rating: ${vendor.rating}</p>
            <p><a href="${pageContext.request.contextPath}/vendor/profile/edit" class="btn btn-secondary btn-sm">Edit Profile</a></p>
        </c:when>
        <c:otherwise>
            <p class="alert alert-warning">Vendor profile attribute is not set or is null. 
                <c:if test="${sessionScope.user.userType == 'VENDOR'}">
                    <a href="${pageContext.request.contextPath}/vendor/profile/create">Create Your Vendor Profile</a>
                </c:if>
            </p>
        </c:otherwise>
    </c:choose>

    <hr/>
    <h3>Your Services (from request attribute 'services'):</h3>
    <p><a href="${pageContext.request.contextPath}/vendor/service/add" class="btn btn-primary btn-sm">Add New Service</a></p>
    <c:choose>
        <c:when test="${not empty services}">
            <p>Number of services: ${services.size()}</p> <!-- JSTL size() for collections -->
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Service Name</th>
                        <th>Type</th>
                        <th>Price</th>
                        <th>Location</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="service" items="${services}">
                        <tr>
                            <td>${service.name}</td>
                            <td>${service.serviceType}</td>
                            <td><fmt:formatNumber value="${service.price}" type="currency" currencySymbol="₹" /></td>
                            <td>${service.location}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/vendor/service/edit?serviceId=${service.id}" class="btn btn-info btn-sm">Edit</a>
                                <form action="${pageContext.request.contextPath}/vendor/service/delete" method="post" style="display:inline;">
                                    <input type="hidden" name="serviceId" value="${service.id}"/>
                                    <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this service?');">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p class="alert alert-info">You have not added any services yet.</p>
        </c:otherwise>
    </c:choose>

    <hr/>
    <h3>Your Bookings (from request attribute 'bookings'):</h3>
     <c:choose>
        <c:when test="${not empty bookings}">
            <p>Number of bookings: ${bookings.size()}</p>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Booking ID</th>
                        <th>Event Date</th>
                        <th>Customer</th>
                        <th>Status</th>
                        <th>Total Cost</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="booking" items="${bookings}">
                        <tr>
                            <td>#${booking.id}</td>
                            <td><fmt:formatDate value="${booking.eventDate}" pattern="yyyy-MM-dd" /> at ${booking.eventTime}</td>
                            <td>User ID: ${booking.userId} <!-- TODO: Fetch User Name --></td>
                            <td>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test='${booking.status == "CONFIRMED"}'>bg-success</c:when>
                                        <c:when test='${booking.status == "PENDING"}'>bg-warning text-dark</c:when>
                                        <c:when test='${booking.status == "CANCELLED"}'>bg-danger</c:when>
                                        <c:otherwise>bg-secondary</c:otherwise>
                                    </c:choose>
                                ">${booking.status}</span>
                            </td>
                            <td><fmt:formatNumber value="${booking.totalCost}" type="currency" currencySymbol="₹"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/booking/details?id=${booking.id}" class="btn btn-sm btn-outline-primary">View Details</a>
                                <!-- Add vendor actions like confirm/reject booking if applicable -->
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p class="alert alert-info">You have no bookings yet.</p>
        </c:otherwise>
    </c:choose>
    
    <p>--- End of Test Page ---</p>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />