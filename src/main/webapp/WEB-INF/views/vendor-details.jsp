<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="${vendor.name}" />
</jsp:include>

<div class="container py-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/vendors">Services</a></li>
            <li class="breadcrumb-item active" aria-current="page">${vendor.name}</li>
        </ol>
    </nav>

    <div class="row">
        <!-- Service Images -->
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

        <!-- Service Details -->
        <div class="col-md-6">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h1 class="mb-0">${vendor.name}</h1>
                <span class="badge bg-primary fs-6">${vendor.type}</span>
            </div>
            
            <div class="mb-3">
                <div class="d-flex align-items-center text-warning mb-2">
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star"></i>
                    <i class="fas fa-star-half-alt"></i>
                    <span class="text-dark ms-2">(28 reviews)</span>
                </div>
            </div>
            
            <h3 class="text-primary mb-3">₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></h3>
            
            <div class="mb-4">
                <h5>Description</h5>
                <p>${vendor.description}</p>
            </div>
            
            <div class="mb-4">
                <h5>Contact Information</h5>
                <p>${vendor.contactInfo}</p>
            </div>
            
            <c:if test="${sessionScope.user != null}">
                <div class="d-grid gap-2">
                    <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                        <input type="hidden" name="vendorId" value="${vendor.id}">
                        <button type="submit" class="btn btn-primary btn-lg w-100">
                            <i class="fas fa-cart-plus me-2"></i>Add to Cart
                        </button>
                    </form>
                </div>
            </c:if>
            
            <c:if test="${sessionScope.user == null}">
                <div class="alert alert-info text-center">
                    <a href="${pageContext.request.contextPath}/login" class="alert-link">Login</a> or 
                    <a href="${pageContext.request.contextPath}/register" class="alert-link">Register</a> to book this service.
                </div>
            </c:if>
        </div>
    </div>

    <!-- Tabs for additional information -->
    <div class="row mt-5">
        <div class="col-12">
            <ul class="nav nav-tabs" id="vendorDetailsTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="details-tab" data-bs-toggle="tab" data-bs-target="#details" type="button" role="tab">Details</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="reviews-tab" data-bs-toggle="tab" data-bs-target="#reviews" type="button" role="tab">Reviews</button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="policies-tab" data-bs-toggle="tab" data-bs-target="#policies" type="button" role="tab">Policies</button>
                </li>
            </ul>
            
            <div class="tab-content p-4 border border-top-0 rounded-bottom" id="vendorDetailsContent">
                <div class="tab-pane fade show active" id="details" role="tabpanel" aria-labelledby="details-tab">
                    <h4>Service Details</h4>
                    <p>This sample text would be replaced with actual service details from the vendor. It could include information about what's included in their service package, any customization options, additional services, etc.</p>
                    
                    <h5>What's Included</h5>
                    <ul>
                        <li>Feature 1</li>
                        <li>Feature 2</li>
                        <li>Feature 3</li>
                        <li>Feature 4</li>
                    </ul>
                </div>
                
                <div class="tab-pane fade" id="reviews" role="tabpanel" aria-labelledby="reviews-tab">
                    <h4>Customer Reviews</h4>
                    
                    <!-- Review 1 -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-2">
                                <div>
                                    <h5 class="mb-0">Great service!</h5>
                                    <div class="text-warning">
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                    </div>
                                </div>
                                <small class="text-muted">2 months ago</small>
                            </div>
                            <p>The quality of service provided was exceptional. I would definitely recommend them for any event.</p>
                            <div class="d-flex align-items-center">
                                <img src="https://randomuser.me/api/portraits/women/32.jpg" class="rounded-circle me-2" width="30" height="30" alt="Reviewer">
                                <span>Sarah Johnson</span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Review 2 -->
                    <div class="card">
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-2">
                                <div>
                                    <h5 class="mb-0">Exceeded expectations</h5>
                                    <div class="text-warning">
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="far fa-star"></i>
                                    </div>
                                </div>
                                <small class="text-muted">3 months ago</small>
                            </div>
                            <p>We were very impressed with the professionalism and attention to detail. They made our event special.</p>
                            <div class="d-flex align-items-center">
                                <img src="https://randomuser.me/api/portraits/men/44.jpg" class="rounded-circle me-2" width="30" height="30" alt="Reviewer">
                                <span>Michael Torres</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="tab-pane fade" id="policies" role="tabpanel" aria-labelledby="policies-tab">
                    <h4>Booking Policies</h4>
                    
                    <div class="mb-4">
                        <h5>Payment Policy</h5>
                        <p>A 50% deposit is required to secure the booking, with the remaining balance due 7 days before the event.</p>
                    </div>
                    
                    <div class="mb-4">
                        <h5>Cancellation Policy</h5>
                        <ul>
                            <li>Cancellations made more than 30 days before the event: Full refund of deposit</li>
                            <li>Cancellations made 15-30 days before the event: 50% refund of deposit</li>
                            <li>Cancellations made less than 15 days before the event: No refund</li>
                        </ul>
                    </div>
                    
                    <div>
                        <h5>Rescheduling Policy</h5>
                        <p>Rescheduling requests must be made at least 14 days before the event date and are subject to availability.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Similar Services -->
    <section class="mt-5">
        <h3 class="section-title mb-4">Similar Services</h3>
        <div class="row row-cols-1 row-cols-md-4 g-4">
            <!-- These would be dynamically populated in a real application -->
            <c:forEach begin="1" end="4">
                <div class="col">
                    <div class="card vendor-card h-100">
                        <img src="https://via.placeholder.com/300x200?text=Similar+Service" class="card-img-top" alt="Similar Service">
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-2">
                                <h5 class="card-title mb-0">Similar Service</h5>
                                <span class="badge bg-primary">${vendor.type}</span>
                            </div>
                            <p class="card-text">Brief description of similar service would go here.</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="text-primary">$XXX.XX</strong>
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