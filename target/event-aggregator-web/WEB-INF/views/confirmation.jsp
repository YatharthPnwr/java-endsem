<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Booking Confirmed" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <!-- Confirmation Message -->
            <div class="card shadow-sm mb-4">
                <div class="card-body text-center p-5">
                    <div class="display-1 text-success mb-4">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <h2 class="fw-bold mb-3">Booking Confirmed!</h2>
                    <p class="lead mb-4">Thank you for your booking. Your order has been received and is being processed.</p>
                    <div class="alert alert-info mb-4">
                        <i class="fas fa-envelope me-2"></i>A confirmation email has been sent to <strong>${user.email}</strong>
                    </div>
                    <div class="d-grid gap-2 col-md-6 mx-auto">
                        <a href="${pageContext.request.contextPath}/user/bookings" class="btn btn-primary">View My Bookings</a>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">Return to Home</a>
                    </div>
                </div>
            </div>

            <!-- Order Details -->
            <div class="card shadow-sm mb-4">
                <div class="card-header">
                    <h4 class="card-title mb-0">Order Details</h4>
                </div>
                <div class="card-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <p class="mb-1 text-muted">Booking Reference</p>
                            <p class="fw-bold">#${booking.id}</p>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <p class="mb-1 text-muted">Booking Date</p>
                            <p class="fw-bold">${booking.createdAt}</p>
                        </div>
                    </div>

                    <div class="row mb-4">
                        <div class="col-md-6">
                            <p class="mb-1 text-muted">Event Date</p>
                            <p class="fw-bold">${booking.eventDate} at ${booking.eventTime}</p>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <p class="mb-1 text-muted">Event Location</p>
                            <p class="fw-bold">${booking.eventLocation}</p>
                        </div>
                    </div>

                    <hr>

                    <h5 class="mb-3">Booked Services</h5>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Service</th>
                                    <th>Type</th>
                                    <th class="text-end">Cost</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="vendor" items="${bookingVendors}">
                                    <tr>
                                        <td>${vendor.name}</td>
                                        <td>${vendor.type}</td>
                                        <td class="text-end">₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td colspan="2" class="text-end fw-bold">Total</td>
                                    <td class="text-end fw-bold">₹<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Next Steps -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <h4 class="card-title mb-3">What Happens Next?</h4>
                    <ol class="mb-0">
                        <li class="mb-2">The service providers will be notified of your booking.</li>
                        <li class="mb-2">You'll receive confirmation from each vendor within 24-48 hours.</li>
                        <li class="mb-2">Vendors may contact you directly to discuss specifics about your event.</li>
                        <li>You can view and manage all your bookings from your <a href="${pageContext.request.contextPath}/user/bookings">account dashboard</a>.</li>
                    </ol>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />