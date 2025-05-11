<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Home" />
</jsp:include>

<!-- Hero Section -->
<section class="hero-section text-center">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <h1 class="display-4 fw-bold mb-4">Plan Your Perfect Event</h1>
                <p class="lead mb-5">Find and book the best vendors for your event all in one place. From venues to catering, photography to decorations, we've got you covered.</p>
                <div class="d-flex flex-wrap justify-content-center gap-3">
                    <a href="${pageContext.request.contextPath}/vendors" class="btn btn-primary btn-lg px-5">Browse Services</a>
                    <a href="#how-it-works" class="btn btn-outline-light btn-lg px-5">How It Works</a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Main Content -->
<div class="container py-5">
    <!-- Search Section has been removed -->

    <!-- Featured Vendors -->
    <section class="mb-5">
        <h2 class="section-title text-center mb-4">Featured Vendors</h2>
        <div class="row">
            <c:forEach items="${featuredVendors}" var="vendor" begin="0" end="3">
                <div class="col-md-6 col-lg-3 mb-4">
                    <div class="card vendor-card shadow-sm h-100">
                        <img src="${vendor.imageUrl != null ? vendor.imageUrl : 'https://via.placeholder.com/300x200?text=Service+Image'}" 
                             class="card-img-top" alt="${vendor.name}">
                        <div class="card-body">
                            <h5 class="card-title">${vendor.name}</h5>
                            <p class="badge bg-info text-dark">${vendor.type}</p>
                            <p class="card-text text-muted">${vendor.description.substring(0, Math.min(80, vendor.description.length()))}...</p>
                        </div>
                        <div class="card-footer bg-white border-0">
                            <a href="${pageContext.request.contextPath}/vendors/view?id=${vendor.id}" class="btn btn-outline-primary w-100">View Details</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
            
            <!-- If no featured vendors are available, show placeholder cards -->
            <c:if test="${empty featuredVendors}">
                <!-- Venue Card -->
                <div class="col-md-6 col-lg-3 mb-4">
                    <div class="card vendor-card shadow-sm h-100">
                        <img src="https://images.unsplash.com/photo-1519167758481-83f550bb49b3?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60" class="card-img-top" alt="Luxury Venue">
                        <div class="card-body">
                            <h5 class="card-title">Luxury Venue</h5>
                            <p class="badge bg-info text-dark">VENUE</p>
                            <p class="card-text text-muted">Elegant venue with stunning views, perfect for weddings and corporate events...</p>
                        </div>
                        <div class="card-footer bg-white border-0">
                            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-primary w-100">View Details</a>
                        </div>
                    </div>
                </div>
                
                <!-- Catering Card -->
                <div class="col-md-6 col-lg-3 mb-4">
                    <div class="card vendor-card shadow-sm h-100">
                        <img src="https://images.unsplash.com/photo-1555244162-803834f70033?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60" class="card-img-top" alt="Gourmet Catering">
                        <div class="card-body">
                            <h5 class="card-title">Gourmet Catering</h5>
                            <p class="badge bg-info text-dark">CATERING</p>
                            <p class="card-text text-muted">Exquisite food and presentation for all occasions, from intimate gatherings to large events...</p>
                        </div>
                        <div class="card-footer bg-white border-0">
                            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-primary w-100">View Details</a>
                        </div>
                    </div>
                </div>
                
                <!-- Photography Card -->
                <div class="col-md-6 col-lg-3 mb-4">
                    <div class="card vendor-card shadow-sm h-100">
                        <img src="https://images.unsplash.com/photo-1520854221256-17451cc331bf?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60" class="card-img-top" alt="Elite Photography">
                        <div class="card-body">
                            <h5 class="card-title">Elite Photography</h5>
                            <p class="badge bg-info text-dark">PHOTOGRAPHY</p>
                            <p class="card-text text-muted">Capturing your special moments with artistic flair and professional equipment...</p>
                        </div>
                        <div class="card-footer bg-white border-0">
                            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-primary w-100">View Details</a>
                        </div>
                    </div>
                </div>
                
                <!-- Decoration Card -->
                <div class="col-md-6 col-lg-3 mb-4">
                    <div class="card vendor-card shadow-sm h-100">
                        <img src="https://images.unsplash.com/photo-1478146896981-b47f11bdce5f?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60" class="card-img-top" alt="Creative Decorations">
                        <div class="card-body">
                            <h5 class="card-title">Creative Decorations</h5>
                            <p class="badge bg-info text-dark">DECOR</p>
                            <p class="card-text text-muted">Transform any space into a stunning event venue with our creative decoration services...</p>
                        </div>
                        <div class="card-footer bg-white border-0">
                            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-primary w-100">View Details</a>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
        
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-primary px-5">View All Services</a>
        </div>
    </section>
    
    <!-- How It Works -->
    <section id="how-it-works" class="mb-5">
        <h2 class="section-title text-center mb-4">How It Works</h2>
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <div class="row g-4">
                    <div class="col-md-4 text-center">
                        <div class="p-3">
                            <div class="display-4 text-primary mb-3">
                                <i class="fas fa-search"></i>
                            </div>
                            <h4>1. Search</h4>
                            <p class="text-muted">Browse through our wide selection of vendors offering various services for your event.</p>
                        </div>
                    </div>
                    <div class="col-md-4 text-center">
                        <div class="p-3">
                            <div class="display-4 text-primary mb-3">
                                <i class="fas fa-calendar-check"></i>
                            </div>
                            <h4>2. Book</h4>
                            <p class="text-muted">Select the vendors you like and add them to your cart to book their services.</p>
                        </div>
                    </div>
                    <div class="col-md-4 text-center">
                        <div class="p-3">
                            <div class="display-4 text-primary mb-3">
                                <i class="fas fa-glass-cheers"></i>
                            </div>
                            <h4>3. Celebrate</h4>
                            <p class="text-muted">Sit back, relax, and enjoy your perfectly planned event with quality vendors.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary px-5">Get Started</a>
        </div>
    </section>
    
    <!-- Testimonials -->
    <section class="mb-5">
        <h2 class="section-title text-center mb-4">What Our Customers Say</h2>
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <div class="row g-4">
                    <div class="col-md-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-body">
                                <div class="mb-2 text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                </div>
                                <p class="card-text">"Event Aggregator made planning our wedding so easy! We found all our vendors in one place and saved so much time. Highly recommended!"</p>
                            </div>
                            <div class="card-footer bg-white">
                                <div class="d-flex align-items-center">
                                    <div class="rounded-circle overflow-hidden me-3" style="width: 40px; height: 40px;">
                                        <img src="https://randomuser.me/api/portraits/women/12.jpg" alt="Customer" class="img-fluid">
                                    </div>
                                    <div>
                                        <h6 class="mb-0">Sarah Johnson</h6>
                                        <small class="text-muted">Wedding Event</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-body">
                                <div class="mb-2 text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                </div>
                                <p class="card-text">"As a corporate event manager, I rely on Event Aggregator for all our company events. The platform is intuitive and the vendors are top-notch professionals."</p>
                            </div>
                            <div class="card-footer bg-white">
                                <div class="d-flex align-items-center">
                                    <div class="rounded-circle overflow-hidden me-3" style="width: 40px; height: 40px;">
                                        <img src="https://randomuser.me/api/portraits/men/32.jpg" alt="Customer" class="img-fluid">
                                    </div>
                                    <div>
                                        <h6 class="mb-0">Michael Davis</h6>
                                        <small class="text-muted">Corporate Event</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-body">
                                <div class="mb-2 text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="far fa-star"></i>
                                </div>
                                <p class="card-text">"The birthday party I planned through Event Aggregator was a huge success! From catering to decorations, everything was perfect. Will definitely use again!"</p>
                            </div>
                            <div class="card-footer bg-white">
                                <div class="d-flex align-items-center">
                                    <div class="rounded-circle overflow-hidden me-3" style="width: 40px; height: 40px;">
                                        <img src="https://randomuser.me/api/portraits/women/28.jpg" alt="Customer" class="img-fluid">
                                    </div>
                                    <div>
                                        <h6 class="mb-0">Amanda Lee</h6>
                                        <small class="text-muted">Birthday Party</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- CTA Section -->
    <section class="bg-primary text-white p-5 rounded-3">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h2 class="mb-3">Ready to Plan Your Perfect Event?</h2>
                <p class="lead mb-md-0">Join Event Aggregator today and discover the best vendors for your upcoming event.</p>
            </div>
            <div class="col-md-4 text-md-end">
                <a href="${pageContext.request.contextPath}/register" class="btn btn-light btn-lg">Sign Up Now</a>
            </div>
        </div>
    </section>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />