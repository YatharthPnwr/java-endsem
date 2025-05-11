<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Vendor Dashboard" />
</jsp:include>

<style>
    .dashboard-stat-card {
        transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
    }
    .dashboard-stat-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
    }
    .dashboard-stat-card .card-title i {
        font-size: 2.5rem; /* Slightly smaller icons for stat cards */
    }
    .dashboard-stat-card .display-6 {
        font-weight: 600;
    }
    .nav-tabs .nav-link {
        font-weight: 500;
    }
    .nav-tabs .nav-link.active {
        border-bottom-width: 3px;
    }
    .table th {
        font-weight: 500;
    }
    .profile-avatar {
        width: 120px;
        height: 120px;
        object-fit: cover;
        border: 3px solid #dee2e6;
    }
</style>

<!-- Page Header -->
<section class="bg-light py-5 mb-4">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="fw-bold">Vendor Dashboard</h1>
                <p class="lead text-muted">Manage your profile, services, and bookings.</p>
            </div>
            <div class="col-md-4 text-md-end">
                <c:if test="${not empty vendor}">
                    <a href="${pageContext.request.contextPath}/vendor/service/add" class="btn btn-primary">
                        <i class="fas fa-plus me-2"></i>Add New Service
                    </a>
                </c:if>
            </div>
        </div>
    </div>
</section>

<!-- Dashboard Content -->
<div class="container py-4">
    <!-- Status Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Vendor Profile Status -->
    <c:if test="${not empty vendor && not vendor.active}">
        <div class="alert alert-warning" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
            Your vendor profile is currently <strong>pending approval</strong> by an administrator or has been deactivated.
            You may not be visible to customers until approved.
        </div>
    </c:if>
    <c:if test="${empty vendor && sessionScope.user.userType == 'VENDOR'}">
        <div class="alert alert-info text-center py-4" role="alert">
            <i class="fas fa-info-circle fa-2x mb-3 d-block"></i>
            <h4 class="alert-heading">Welcome, Vendor!</h4>
            <p>You haven't created your vendor profile yet. A complete profile helps customers find you.</p>
            <a href="${pageContext.request.contextPath}/vendor/profile/create" class="btn btn-primary">
                <i class="fas fa-store me-2"></i>Create Your Vendor Profile
            </a>
        </div>
    </c:if>

    <c:if test="${not empty vendor}">
        <div class="row">
            <!-- Sidebar - Vendor Profile Summary -->
            <div class="col-lg-4 mb-4">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-id-card me-2"></i>Your Profile</h5>
                    </div>
                    <div class="card-body">
                        <div class="text-center mb-3">
                            <img src="${not empty vendor.imageUrl ? vendor.imageUrl : 'https://via.placeholder.com/150?text=Logo'}"
                                 alt="${vendor.name}" class="img-thumbnail rounded-circle profile-avatar mb-2">
                            <h4 class="card-title">${vendor.name}</h4>
                            <p class="text-muted mb-1">${vendor.type}</p>
                            <span class="badge ${vendor.active ? 'bg-success' : 'bg-warning text-dark'}">
                                ${vendor.active ? 'Active & Approved' : 'Pending Approval / Inactive'}
                            </span>
                        </div>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><i class="fas fa-map-marker-alt me-2 text-muted"></i>${vendor.location}</li>
                            <li class="list-group-item"><i class="fas fa-envelope me-2 text-muted"></i>${fn:split(vendor.contactInfo, ',')[0]}</li>
                            <c:if test="${fn:length(fn:split(vendor.contactInfo, ',')) > 1}">
                                <li class="list-group-item"><i class="fas fa-phone me-2 text-muted"></i>${fn:split(vendor.contactInfo, ',')[1]}</li>
                            </c:if>
                            <li class="list-group-item"><i class="fas fa-star me-2 text-warning"></i>Rating: ${vendor.rating > 0 ? vendor.rating : 'N/A'}</li>
                        </ul>
                         <div class="text-center mt-3">
                            <a href="${pageContext.request.contextPath}/vendor/profile/edit" class="btn btn-outline-secondary">
                                <i class="fas fa-edit me-1"></i>Edit Profile
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Main Content Area -->
            <div class="col-lg-8">
                <!-- Quick Stats Cards -->
                <div class="row mb-4">
                    <div class="col-md-6 mb-3">
                        <div class="card shadow-sm text-center dashboard-stat-card">
                            <div class="card-body">
                                <h3 class="card-title display-6 text-primary">${fn:length(services)}</h3>
                                <p class="card-text text-muted"><i class="fas fa-concierge-bell me-2"></i>Services Offered</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <div class="card shadow-sm text-center dashboard-stat-card">
                            <div class="card-body">
                                <h3 class="card-title display-6 text-success">${fn:length(bookings)}</h3>
                                <p class="card-text text-muted"><i class="fas fa-calendar-check me-2"></i>Bookings Received</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Tabs for Services and Bookings -->
                <ul class="nav nav-tabs mb-3" id="vendorDashboardTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="services-tab" data-bs-toggle="tab" data-bs-target="#services-content" type="button" role="tab" aria-controls="services-content" aria-selected="true">
                            <i class="fas fa-concierge-bell me-1"></i>My Services
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="bookings-tab" data-bs-toggle="tab" data-bs-target="#bookings-content-tab" type="button" role="tab" aria-controls="bookings-content-tab" aria-selected="false">
                            <i class="fas fa-calendar-alt me-1"></i>Bookings
                        </button>
                    </li>
                </ul>

                <div class="tab-content" id="vendorDashboardTabsContent">
                    <!-- My Services Tab -->
                    <div class="tab-pane fade show active" id="services-content" role="tabpanel" aria-labelledby="services-tab">
                        <div class="card shadow-sm">
                            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Manage Your Services</h5>
                                <a href="${pageContext.request.contextPath}/vendor/service/add" class="btn btn-primary btn-sm">
                                    <i class="fas fa-plus me-1"></i>Add New Service
                                </a>
                            </div>
                            <div class="card-body p-0">
                                <c:choose>
                                    <c:when test="${not empty services}">
                                        <div class="table-responsive">
                                            <table class="table table-hover mb-0">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>Name</th>
                                                        <th>Type</th>
                                                        <th>Price</th>
                                                        <th>Status</th>
                                                        <th class="text-end">Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="service" items="${services}">
                                                        <tr>
                                                            <td>${fn:escapeXml(service.name)}</td>
                                                            <td><span class="badge bg-info text-dark">${fn:escapeXml(service.serviceType)}</span></td>
                                                            <td><fmt:formatNumber value="${service.price}" type="currency" currencySymbol="₹" /></td>
                                                            <td>
                                                                <span class="badge ${service.active ? 'bg-success' : 'bg-secondary'}">
                                                                    ${service.active ? 'Active' : 'Inactive'}
                                                                </span>
                                                            </td>
                                                            <td class="text-end">
                                                                <a href="${pageContext.request.contextPath}/vendor/service/edit?serviceId=${service.id}" class="btn btn-outline-info btn-sm" title="Edit">
                                                                    <i class="fas fa-edit"></i>
                                                                </a>
                                                                <form action="${pageContext.request.contextPath}/vendor/service/delete" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this service: ${fn:escapeXml(service.name)}?');">
                                                                    <input type="hidden" name="serviceId" value="${service.id}"/>
                                                                    <button type="submit" class="btn btn-outline-danger btn-sm" title="Delete">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                </form>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="p-4 text-center">
                                            <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">You haven't added any services yet.</p>
                                            <a href="${pageContext.request.contextPath}/vendor/service/add" class="btn btn-primary">Add Your First Service</a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- Bookings Tab -->
                    <div class="tab-pane fade" id="bookings-content-tab" role="tabpanel" aria-labelledby="bookings-tab">
                        <div class="card shadow-sm">
                            <div class="card-header bg-white">
                                <h5 class="mb-0">Received Bookings</h5>
                            </div>
                            <div class="card-body p-0">
                                <c:choose>
                                    <c:when test="${not empty bookings}">
                                         <div class="table-responsive">
                                            <table class="table table-hover mb-0">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>Booking ID</th>
                                                        <th>Event Date</th>
                                                        <th>Customer (User ID)</th>
                                                        <th>Status</th>
                                                        <th class="text-end">Total</th>
                                                        <th class="text-end">Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="booking" items="${bookings}">
                                                        <tr>
                                                            <td>#${booking.id}</td>
                                                            <td><fmt:formatDate value="${booking.eventDate}" pattern="MMM dd, yyyy" /> <c:if test="${not empty booking.eventTime}">at ${booking.eventTime}</c:if></td>
                                                            <td>${booking.userId}</td> <%-- TODO: Fetch User Name via a utility method or by joining in DAO and adding to Booking model --%>
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
                                                            <td class="text-end"><fmt:formatNumber value="${booking.totalCost}" type="currency" currencySymbol="₹"/></td>
                                                            <td class="text-end">
                                                                <a href="${pageContext.request.contextPath}/booking/${booking.id}" class="btn btn-outline-primary btn-sm" title="View Details">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
                                                                <%-- Example actions for vendor:
                                                                <c:if test="${booking.status eq 'PENDING'}">
                                                                    <button class="btn btn-success btn-sm" title="Confirm"><i class="fas fa-check"></i></button>
                                                                    <button class="btn btn-danger btn-sm" title="Reject"><i class="fas fa-times"></i></button>
                                                                </c:if>
                                                                --%>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="p-4 text-center">
                                             <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">You have no bookings yet.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div> <!-- end tab content -->
            </div> <!-- end col-lg-8 -->
        </div> <!-- end main row -->
    </c:if> <%-- End check for not empty vendor --%>
</div> <!-- end container -->

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />