<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="${vendor.name}" />
</jsp:include>

<div class="container py-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/vendors">Vendors</a></li>
            <li class="breadcrumb-item active" aria-current="page">${vendor.name}</li>
        </ol>
    </nav>

    <div class="row">
        <!-- Vendor Image -->
        <div class="col-md-6 mb-4">
            <div class="card">
                <c:choose>
                    <c:when test="${not empty vendor.imageUrl}">
                        <img src="${vendor.imageUrl}" class="card-img-top img-fluid" alt="${vendor.name}" style="height: 400px; object-fit: cover;">
                    </c:when>
                    <c:otherwise>
                        <img src="https://via.placeholder.com/800x400?text=No+Image+Available" class="card-img-top" alt="No Image Available">
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Vendor Details -->
        <div class="col-md-6">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h1 class="mb-0">${vendor.name}</h1>
                <span class="badge bg-primary fs-6">${vendor.type}</span>
            </div>
            
            <div class="mb-3">
                 <div class="d-flex align-items-center text-warning mb-2">
                    <c:set var="fullStars" value="${fn:substringBefore(vendor.rating, '.')}" />
                    <c:set var="decimalPart" value="${fn:substringAfter(vendor.rating, '.')}" />
                    <c:set var="hasHalfStar" value="${decimalPart >= 5}" />
                    <c:set var="emptyStars" value="${5 - fullStars - (hasHalfStar ? 1 : 0)}" />

                    <c:forEach begin="1" end="${fullStars}">
                        <i class="fas fa-star"></i>
                    </c:forEach>
                    <c:if test="${hasHalfStar}">
                        <i class="fas fa-star-half-alt"></i>
                    </c:if>
                    <c:forEach begin="1" end="${emptyStars}">
                        <i class="far fa-star"></i>
                    </c:forEach>
                    <span class="text-dark ms-2">(${vendor.rating > 0 ? vendor.rating : 'No'} reviews)</span>
                </div>
            </div>
            
            <h5 class="text-muted mb-1">Starting from (Base Package):</h5>
            <h3 class="text-primary mb-3">₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></h3>
            
            <div class="mb-4">
                <h5>About ${vendor.name}</h5>
                <p>${vendor.description}</p>
            </div>
            
            <div class="mb-4">
                <h5>Contact Information</h5>
                <%-- Split contactInfo assuming it's "email, phone" --%>
                <c:set var="contactParts" value="${fn:split(vendor.contactInfo, ',')}" />
                <p><i class="fas fa-envelope me-2"></i>${fn:trim(contactParts[0])}</p>
                <c:if test="${fn:length(contactParts) > 1}">
                    <p><i class="fas fa-phone me-2"></i>${fn:trim(contactParts[1])}</p>
                </c:if>
                 <p><i class="fas fa-map-marker-alt me-2"></i>${vendor.location}</p>
            </div>
            
            <c:if test="${sessionScope.user != null}">
                <div class="d-grid gap-2">
                    <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                        <input type="hidden" name="vendorId" value="${vendor.id}">
                        <button type="submit" class="btn btn-primary btn-lg w-100">
                            <i class="fas fa-cart-plus me-2"></i>Add ${vendor.name}'s Base Package to Cart
                        </button>
                    </form>
                </div>
            </c:if>
            
            <c:if test="${sessionScope.user == null}">
                <div class="alert alert-info text-center">
                    <a href="${pageContext.request.contextPath}/login" class="alert-link">Login</a> or 
                    <a href="${pageContext.request.contextPath}/register" class="alert-link">Register</a> to book this vendor.
                </div>
            </c:if>
        </div>
    </div>

    <!-- Tabs for additional information -->
    <div class="row mt-5">
        <div class="col-12">
            <ul class="nav nav-tabs" id="vendorDetailsTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="services-offered-tab" data-bs-toggle="tab" data-bs-target="#services-offered" type="button" role="tab">Services Offered</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="reviews-tab" data-bs-toggle="tab" data-bs-target="#reviews" type="button" role="tab">Reviews</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="policies-tab" data-bs-toggle="tab" data-bs-target="#policies" type="button" role="tab">Policies</button>
                </li>
            </ul>
            
            <div class="tab-content p-4 border border-top-0 rounded-bottom" id="vendorDetailsContent">
                <!-- SERVICES OFFERED TAB (MODIFIED/ADDED) -->
                <div class="tab-pane fade show active" id="services-offered" role="tabpanel" aria-labelledby="services-offered-tab">
                    <h4>Specific Services by ${vendor.name}</h4>
                    <c:choose>
                        <c:when test="${not empty services}">
                            <div class="list-group mt-3">
                                <c:forEach items="${services}" var="service">
                                    <div class="list-group-item list-group-item-action flex-column align-items-start mb-3 shadow-sm">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1">${service.name}</h5>
                                            <small class="text-primary fw-bold fs-5">₹<fmt:formatNumber value="${service.price}" pattern="#,##0.00"/></small>
                                        </div>
                                        <p class="mb-1">${service.description}</p>
                                        <small class="text-muted">Service Type: ${service.serviceType}</small><br/>
                                        <c:if test="${not empty service.location && service.location ne vendor.location}">
                                            <small class="text-muted">Location: ${service.location}</small><br/>
                                        </c:if>
                                        <c:if test="${not empty service.imageUrl}">
                                            <img src="${service.imageUrl}" alt="${service.name}" class="img-thumbnail mt-2" style="max-width: 200px; max-height: 150px;">
                                        </c:if>
                                        <%-- 
                                        // OPTIONAL: Add to cart for individual service
                                        // This would require cart logic to handle service IDs
                                        <c:if test="${sessionScope.user != null && service.active}">
                                            <form action="${pageContext.request.contextPath}/add-service-to-cart" method="post" class="mt-2 text-end">
                                                <input type="hidden" name="vendorServiceId" value="${service.id}">
                                                <button type="submit" class="btn btn-sm btn-success">
                                                    <i class="fas fa-cart-plus"></i> Add This Service
                                                </button>
                                            </form>
                                        </c:if>
                                        --%>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info mt-3">
                                ${vendor.name} has not listed any specific additional services yet. You can book their base package offering shown above.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div class="tab-pane fade" id="reviews" role="tabpanel" aria-labelledby="reviews-tab">
                    <h4>Customer Reviews</h4>
                    <%-- Placeholder for actual review system --%>
                    <div class="card mb-3">
                        <div class="card-body">
                            <p>No reviews available for this vendor yet.</p>
                        </div>
                    </div>
                </div>
                
                <div class="tab-pane fade" id="policies" role="tabpanel" aria-labelledby="policies-tab">
                    <h4>Booking Policies</h4>
                    <%-- Placeholder - This should be manageable by the vendor --%>
                     <p>Please contact ${vendor.name} directly for their specific booking, payment, and cancellation policies.</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Similar Vendors (Adjusted from "Similar Services") -->
    <section class="mt-5">
        <h3 class="section-title mb-4">Similar Vendors</h3>
        <div class="row row-cols-1 row-cols-md-4 g-4">
            <%-- This section would ideally fetch actual similar vendors based on type/location --%>
            <c:forEach begin="1" end="4"> 
                <div class="col">
                    <div class="card vendor-card h-100">
                        <img src="https://via.placeholder.com/300x200?text=Similar+Vendor" class="card-img-top" alt="Similar Vendor">
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-2">
                                <h5 class="card-title mb-0">Another Vendor</h5>
                                <span class="badge bg-primary">${vendor.type}</span> 
                            </div>
                            <p class="card-text">Brief description of another vendor.</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="text-primary">From $XXX.XX</strong>
                                <a href="#" class="btn btn-sm btn-outline-primary">View Details</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />