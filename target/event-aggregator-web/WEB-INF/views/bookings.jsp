<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="My Bookings" />
</jsp:include>

<!-- Page Header -->
<section class="bg-light py-5 mb-4">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="fw-bold">My Bookings</h1>
                <p class="lead text-muted">View and manage your event bookings</p>
            </div>
            <div class="col-md-4 text-md-end">
                <a href="${pageContext.request.contextPath}/vendors" class="btn btn-primary">
                    <i class="fas fa-plus me-2"></i>Book New Service
                </a>
            </div>
        </div>
    </div>
</section>

<!-- Bookings Content -->
<div class="container" id="bookings-content">
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

    <c:choose>
        <c:when test="${empty bookings}">
            <div class="card shadow-sm mb-4">
                <div class="card-body text-center py-5">
                    <i class="fas fa-calendar-alt fa-3x text-muted mb-3"></i>
                    <h4>No Bookings Found</h4>
                    <p class="text-muted">You haven't made any bookings yet.</p>
                    <a href="${pageContext.request.contextPath}/vendors" class="btn btn-primary">Browse Services</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <!-- Bookings Table -->
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Your Bookings</h5>
                    <button onclick="printAllBookings()" class="btn btn-sm btn-outline-secondary">
                        <i class="fas fa-print me-1"></i> Print All
                    </button>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Booking Date</th>
                                <th scope="col">Event Date</th>
                                <th scope="col">Location</th>
                                <th scope="col">Total Cost</th>
                                <th scope="col">Status</th>
                                <th scope="col" class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${bookings}" var="booking">
                                <tr>
                                    <td>#${booking.id}</td>
                                    <td><fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy" /></td>
                                    <td><fmt:formatDate value="${booking.eventDate}" pattern="MMM dd, yyyy" /></td>
                                    <td>${booking.eventLocation}</td>
                                    <td>₹<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></td>
                                    <td>
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
                                    </td>
                                    <td class="text-end">
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/booking/${booking.id}" class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i><span class="d-none d-md-inline ms-1">Details</span>
                                            </a>
                                            <button onclick="printBooking('${booking.id}')" class="btn btn-sm btn-outline-secondary">
                                                <i class="fas fa-print"></i><span class="d-none d-md-inline ms-1">Print</span>
                                            </button>
                                            <c:if test="${booking.status ne 'CANCELLED' && booking.eventDate gt now}">
                                                <button type="button" class="btn btn-sm btn-outline-danger" 
                                                        data-bs-toggle="modal" data-bs-target="#cancelModal${booking.id}">
                                                    <i class="fas fa-times"></i><span class="d-none d-md-inline ms-1">Cancel</span>
                                                </button>
                                            </c:if>
                                        </div>
                                        
                                        <!-- Cancel Booking Modal -->
                                        <c:if test="${booking.status ne 'CANCELLED' && booking.eventDate gt now}">
                                            <div class="modal fade" id="cancelModal${booking.id}" tabindex="-1" aria-labelledby="cancelModalLabel" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title" id="cancelModalLabel">Confirm Cancellation</h5>
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
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Printable Templates -->
<div class="d-none">
    <c:forEach items="${bookings}" var="booking">
        <div id="printable-booking-${booking.id}" class="p-4">
            <div class="text-center mb-4">
                <h2>Event Booking Invoice</h2>
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
                    <c:forEach items="${booking.vendorIds}" var="vendorId" varStatus="status">
                        <c:set var="vendor" value="${null}" />
                        <c:forEach items="${vendors}" var="v">
                            <c:if test="${v.id eq vendorId}">
                                <c:set var="vendor" value="${v}" />
                            </c:if>
                        </c:forEach>
                        
                        <c:if test="${vendor ne null}">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>${vendor.name}</td>
                                <td>${vendor.type}</td>
                                <td>₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:if>
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
                <p><small>This is a computer-generated invoice. No signature is required.</small></p>
                <p><small>Printed on: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm:ss" /></small></p>
            </div>
        </div>
    </c:forEach>
    
    <div id="printable-all-bookings" class="p-4">
        <div class="text-center mb-4">
            <h2>All Bookings Report</h2>
            <p>Event Aggregator Services</p>
        </div>
        
        <table class="table table-bordered mb-4">
            <thead>
                <tr>
                    <th>Booking ID</th>
                    <th>Booking Date</th>
                    <th>Event Date</th>
                    <th>Location</th>
                    <th>Status</th>
                    <th>Total Cost</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${bookings}" var="booking">
                    <tr>
                        <td>#${booking.id}</td>
                        <td><fmt:formatDate value="${booking.bookingDate}" pattern="MM/dd/yyyy" /></td>
                        <td><fmt:formatDate value="${booking.eventDate}" pattern="MM/dd/yyyy" /></td>
                        <td>${booking.eventLocation}</td>
                        <td>${booking.status}</td>
                        <td>₹<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <div class="mt-5">
            <p><small>This is a computer-generated report. No signature is required.</small></p>
            <p><small>Printed on: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm:ss" /></small></p>
        </div>
    </div>
</div>

<!-- JavaScript for Print Functionality -->
<script>
    // Set current date for comparison with event dates
    var now = new Date();
    document.addEventListener('DOMContentLoaded', function() {
        // Add now to the page context for JSTL comparison
        // This is handled on the server side with ${now}
    });
    
    function printBooking(bookingId) {
        var printContent = document.getElementById('printable-booking-' + bookingId).innerHTML;
        var originalContent = document.body.innerHTML;
        
        document.body.innerHTML = '<div class="container">' + printContent + '</div>';
        window.print();
        document.body.innerHTML = originalContent;
        
        // Reinitialize Bootstrap components after restoring original content
        initializeBootstrap();
    }
    
    function printAllBookings() {
        var printContent = document.getElementById('printable-all-bookings').innerHTML;
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