<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Booking Details" />
</jsp:include>

<!-- Page Header -->
<section class="bg-light py-5 mb-4">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="fw-bold">Booking #${booking.id}</h1>
                <p class="lead text-muted">View your booking details</p>
            </div>
            <div class="col-md-4 text-md-end">
                <a href="${pageContext.request.contextPath}/user/bookings" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Back to All Bookings
                </a>
            </div>
        </div>
    </div>
</section>

<!-- Booking Details Content -->
<div class="container mb-5">
    <!-- Status Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Booking Summary Card -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Booking Summary</h5>
            <div>
                <button onclick="printBookingDetails()" class="btn btn-sm btn-outline-secondary me-2">
                    <i class="fas fa-print me-1"></i> Print
                </button>
                <c:if test="${booking.status ne 'CANCELLED' && booking.eventDate gt now}">
                    <button type="button" class="btn btn-sm btn-outline-danger" 
                            data-bs-toggle="modal" data-bs-target="#cancelBookingModal">
                        <i class="fas fa-times me-1"></i> Cancel Booking
                    </button>
                </c:if>
            </div>
        </div>
        <div class="card-body">
            <div class="row mb-4">
                <div class="col-md-6">
                    <h6 class="text-muted mb-2">Booking Information</h6>
                    <div class="mb-3">
                        <p class="mb-1"><strong>Booking ID:</strong> #${booking.id}</p>
                        <p class="mb-1"><strong>Booking Date:</strong> <fmt:formatDate value="${booking.bookingDate}" pattern="MMMM dd, yyyy" /></p>
                        <p class="mb-0">
                            <strong>Status:</strong>
                            <c:choose>
                                <c:when test="${booking.status eq 'CONFIRMED'}">
                                    <span class="badge bg-success">Confirmed</span>
                                </c:when>
                                <c:when test="${booking.status eq 'CANCELLED'}">
                                    <span class="badge bg-danger">Cancelled</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-warning text-dark">Pending</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
                <div class="col-md-6">
                    <h6 class="text-muted mb-2">Event Information</h6>
                    <div class="mb-3">
                        <p class="mb-1"><strong>Event Date:</strong> <fmt:formatDate value="${booking.eventDate}" pattern="MMMM dd, yyyy" /></p>
                        <p class="mb-0"><strong>Location:</strong> ${booking.eventLocation}</p>
                    </div>
                </div>
            </div>

            <hr>

            <h6 class="text-muted mb-3">Booked Services</h6>
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead class="table-light">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Service</th>
                            <th scope="col">Type</th>
                            <th scope="col">Price</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${bookingVendors}" var="vendor" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/vendor/${vendor.id}" class="text-decoration-none">
                                        ${vendor.name}
                                    </a>
                                </td>
                                <td>${vendor.type}</td>
                                <td>₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="3" class="text-end"><strong>Total:</strong></td>
                            <td><strong>₹<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></strong></td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>

    <!-- Additional Information Card -->
    <div class="card shadow-sm">
        <div class="card-header bg-white">
            <h5 class="card-title mb-0">Additional Information</h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${booking.status eq 'PENDING'}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        Your booking is currently pending confirmation. The vendors will be in touch shortly.
                    </div>
                </c:when>
                <c:when test="${booking.status eq 'CONFIRMED'}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle me-2"></i>
                        Your booking has been confirmed! All services are scheduled for your event.
                    </div>
                </c:when>
                <c:when test="${booking.status eq 'CANCELLED'}">
                    <div class="alert alert-danger">
                        <i class="fas fa-times-circle me-2"></i>
                        This booking has been cancelled.
                    </div>
                </c:when>
            </c:choose>

            <h6 class="mb-3">Vendor Contact Information</h6>
            <div class="row row-cols-1 row-cols-md-2 g-3">
                <c:forEach items="${bookingVendors}" var="vendor">
                    <div class="col">
                        <div class="card h-100">
                            <div class="card-body">
                                <h6>${vendor.name}</h6>
                                <p class="text-muted mb-1">${vendor.type}</p>
                                <p class="mb-0"><small><i class="fas fa-envelope me-1"></i> ${vendor.contactInfo}</small></p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<!-- Cancel Booking Modal -->
<c:if test="${booking.status ne 'CANCELLED' && booking.eventDate gt now}">
    <div class="modal fade" id="cancelBookingModal" tabindex="-1" aria-labelledby="cancelBookingModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="cancelBookingModalLabel">Confirm Cancellation</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to cancel booking #${booking.id}?</p>
                    <p><strong>Event Date:</strong> <fmt:formatDate value="${booking.eventDate}" pattern="MMM dd, yyyy" /></p>
                    <p><strong>Location:</strong> ${booking.eventLocation}</p>
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        This action cannot be undone.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <form action="${pageContext.request.contextPath}/cancel-booking" method="post">
                        <input type="hidden" name="bookingId" value="${booking.id}">
                        <button type="submit" class="btn btn-danger">Cancel Booking</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Printable Template -->
<div class="d-none">
    <div id="printable-booking" class="p-4">
        <div class="text-center mb-4">
            <h2>Event Booking Details</h2>
            <p>Event Aggregator Services</p>
        </div>
        
        <div class="row mb-4">
            <div class="col-md-6">
                <h5>Booking Information</h5>
                <p><strong>Booking ID:</strong> #${booking.id}</p>
                <p><strong>Booking Date:</strong> <fmt:formatDate value="${booking.bookingDate}" pattern="MMMM dd, yyyy" /></p>
                <p><strong>Status:</strong> ${booking.status}</p>
            </div>
            <div class="col-md-6">
                <h5>Event Details</h5>
                <p><strong>Event Date:</strong> <fmt:formatDate value="${booking.eventDate}" pattern="MMMM dd, yyyy" /></p>
                <p><strong>Location:</strong> ${booking.eventLocation}</p>
            </div>
        </div>
        
        <h5>Services</h5>
        <table class="table table-bordered mb-4">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Service</th>
                    <th>Type</th>
                    <th>Cost</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${bookingVendors}" var="vendor" varStatus="status">
                    <tr>
                        <td>${status.index + 1}</td>
                        <td>${vendor.name}</td>
                        <td>${vendor.type}</td>
                        <td>₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></td>
                    </tr>
                </c:forEach>
            </tbody>
            <tfoot>
                <tr>
                    <th colspan="3" class="text-end">Total:</th>
                    <th>₹<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></th>
                </tr>
            </tfoot>
        </table>
        
        <div class="mt-5">
            <p><small>This is a computer-generated document. No signature is required.</small></p>
            <p><small>Printed on: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm:ss" /></small></p>
        </div>
    </div>
</div>

<!-- JavaScript for Print Functionality -->
<script>
    function printBookingDetails() {
        var printContent = document.getElementById('printable-booking').innerHTML;
        var originalContent = document.body.innerHTML;
        
        document.body.innerHTML = '<div class="container">' + printContent + '</div>';
        window.print();
        document.body.innerHTML = originalContent;
        
        // Reinitialize Bootstrap components after restoring original content
        initializeBootstrap();
    }
    
    function initializeBootstrap() {
        // Re-initialize Bootstrap tooltips, popovers, etc.
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl)
        });
    }
</script>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />