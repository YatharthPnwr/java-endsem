<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Checkout" />
</jsp:include>

<div class="container py-5">
    <div class="row">
        <div class="col-lg-8">
            <h2 class="mb-4">Checkout</h2>

            <!-- Checkout Form -->
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h4 class="card-title mb-4">Event Details</h4>

                    <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="eventDate" class="form-label">Event Date <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="eventDate" name="eventDate" required
                                    min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                            </div>
                            <div class="col-md-6">
                                <label for="eventTime" class="form-label">Event Time</label>
                                <input type="time" class="form-control" id="eventTime" name="eventTime">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="eventLocation" class="form-label">Event Location <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="eventLocation" name="eventLocation" rows="2" required
                                placeholder="Enter the full address of your event location"></textarea>
                        </div>

                        <div class="mb-3">
                            <label for="guestCount" class="form-label">Expected Number of Guests</label>
                            <input type="number" class="form-control" id="guestCount" name="guestCount" min="1">
                        </div>

                        <div class="mb-3">
                            <label for="specialRequirements" class="form-label">Special Requirements or Notes</label>
                            <textarea class="form-control" id="specialRequirements" name="specialRequirements" rows="3"
                                placeholder="Any special requests or details that vendors should know about"></textarea>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Contact Details -->
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h4 class="card-title mb-4">Contact Details</h4>
                    <p class="text-muted">We'll use this information to contact you about your booking.</p>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="name" class="form-label">Name</label>
                            <input type="text" class="form-control" id="name" name="name" form="checkoutForm"
                                value="${sessionScope.user.name}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" form="checkoutForm"
                                value="${sessionScope.user.email}" readonly>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone <span class="text-danger">*</span></label>
                        <input type="tel" class="form-control" id="phone" name="phone" form="checkoutForm"
                            value="${sessionScope.user.phone}" required>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="contactConsent" name="contactConsent" form="checkoutForm">
                        <label class="form-check-label" for="contactConsent">
                            I agree to be contacted by vendors regarding my booking.
                        </label>
                    </div>
                </div>
            </div>

            <!-- Payment Information -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <h4 class="card-title mb-4">Payment Information</h4>
                    <p class="text-muted mb-4">Your payment information will be securely processed.</p>

                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        For demonstration purposes, no actual payment will be processed. Payment will be handled directly by vendors.
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-4 mt-4 mt-lg-0">
            <!-- Order Summary -->
            <div class="card shadow-sm sticky-top" style="top: 20px">
                <div class="card-header bg-primary text-white">
                    <h4 class="card-title mb-0">Order Summary</h4>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty cart || empty cart.vendorIds}">
                            <div class="alert alert-warning">
                                Your cart is empty. Please add services before checkout.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Services List -->
                            <div class="mb-3">
                                <h5 class="mb-3">Services</h5>
                                <c:forEach var="vendor" items="${cartVendors}">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <div>
                                            <strong>${vendor.name}</strong>
                                            <div><small class="text-muted">${vendor.type}</small></div>
                                        </div>
                                        <div class="text-end">
                                            ₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <hr>

                            <!-- Total -->
                            <div class="d-flex justify-content-between mb-3">
                                <h5>Total:</h5>
                                <h5>₹<fmt:formatNumber value="${cart.totalCost}" pattern="#,##0.00"/></h5>
                            </div>

                            <!-- Terms Agreement -->
                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" id="termsAgreement" name="termsAgreement" form="checkoutForm" required>
                                <label class="form-check-label" for="termsAgreement">
                                    I agree to the <a href="#" class="text-decoration-none">Terms of Service</a> and <a href="#" class="text-decoration-none">Privacy Policy</a>
                                </label>
                            </div>

                            <!-- Place Order Button -->
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary btn-lg" form="checkoutForm">Complete Booking</button>
                            </div>

                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">
                                    <i class="fas fa-arrow-left me-1"></i>Return to cart
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />