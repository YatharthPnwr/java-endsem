<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Browse Vendors" />
</jsp:include>

<div class="container py-5">
    <div class="row">
        <!-- Filters Sidebar -->
        <div class="col-lg-3 mb-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h5 class="card-title mb-4">Filter Options</h5>
                    <form action="${pageContext.request.contextPath}/vendors" method="get" id="filterForm">
                        <!-- Search -->
                        <div class="mb-4">
                            <label for="searchQuery" class="form-label fw-bold">Search</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="searchQuery" name="query" 
                                    value="${param.query}" placeholder="Search vendors...">
                                <button class="btn btn-outline-primary" type="submit">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </div>
                        
                        <!-- Event Type Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">Event Type</label>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="eventType" value="wedding" id="wedding"
                                    ${param.eventType == 'wedding' ? 'checked' : ''}>
                                <label class="form-check-label" for="wedding">Wedding</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="eventType" value="corporate" id="corporate"
                                    ${param.eventType == 'corporate' ? 'checked' : ''}>
                                <label class="form-check-label" for="corporate">Corporate</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="eventType" value="birthday" id="birthday"
                                    ${param.eventType == 'birthday' ? 'checked' : ''}>
                                <label class="form-check-label" for="birthday">Birthday</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="eventType" value="party" id="party"
                                    ${param.eventType == 'party' ? 'checked' : ''}>
                                <label class="form-check-label" for="party">Party</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="eventType" value="other" id="otherEvent"
                                    ${param.eventType == 'other' ? 'checked' : ''}>
                                <label class="form-check-label" for="otherEvent">Other</label>
                            </div>
                        </div>
                        
                        <!-- Service Category Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">Service Category</label>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="category" value="catering" id="catering"
                                    ${param.category == 'catering' ? 'checked' : ''}>
                                <label class="form-check-label" for="catering">Catering</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="category" value="venue" id="venue"
                                    ${param.category == 'venue' ? 'checked' : ''}>
                                <label class="form-check-label" for="venue">Venue</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="category" value="photography" id="photography"
                                    ${param.category == 'photography' ? 'checked' : ''}>
                                <label class="form-check-label" for="photography">Photography</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="category" value="entertainment" id="entertainment"
                                    ${param.category == 'entertainment' ? 'checked' : ''}>
                                <label class="form-check-label" for="entertainment">Entertainment</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input filter-check" type="checkbox" name="category" value="decoration" id="decoration"
                                    ${param.category == 'decoration' ? 'checked' : ''}>
                                <label class="form-check-label" for="decoration">Decoration</label>
                            </div>
                        </div>
                        
                        <!-- Price Range Filter -->
                        <div class="mb-4">
                            <label for="priceRange" class="form-label fw-bold">Price Range</label>
                            <div class="d-flex align-items-center gap-2 mb-2">
                                <span>₹0</span>
                                <input type="range" class="form-range flex-grow-1" id="priceRange" name="maxPrice" 
                                    min="0" max="10000" step="100" value="${empty param.maxPrice ? '10000' : param.maxPrice}">
                                <span>₹10,000+</span>
                            </div>
                            <div class="text-center">
                                <span id="priceRangeValue">₹${empty param.maxPrice ? '10000' : param.maxPrice}+</span>
                            </div>
                        </div>
                        
                        <!-- Rating Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">Minimum Rating</label>
                            <div class="rating-filter mb-2">
                                <div class="form-check">
                                    <input class="form-check-input filter-check" type="radio" name="rating" value="5" id="rating5"
                                        ${param.rating == '5' ? 'checked' : ''}>
                                    <label class="form-check-label" for="rating5">
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input filter-check" type="radio" name="rating" value="4" id="rating4"
                                        ${param.rating == '4' || empty param.rating ? 'checked' : ''}>
                                    <label class="form-check-label" for="rating4">
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="far fa-star text-warning"></i>
                                        & up
                                    </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input filter-check" type="radio" name="rating" value="3" id="rating3"
                                        ${param.rating == '3' ? 'checked' : ''}>
                                    <label class="form-check-label" for="rating3">
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="fas fa-star text-warning"></i>
                                        <i class="far fa-star text-warning"></i>
                                        <i class="far fa-star text-warning"></i>
                                        & up
                                    </label>
                                </div>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Apply Filters</button>
                            <button type="reset" class="btn btn-outline-secondary" id="resetFilters">Reset Filters</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <!-- Vendors Listing -->
        <div class="col-lg-9">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Available Services</h2>
                <div class="d-flex align-items-center">
                    <label for="sortBy" class="me-2">Sort by:</label>
                    <select class="form-select" id="sortBy" name="sortBy">
                        <option value="recommended">Recommended</option>
                        <option value="priceAsc">Price: Low to High</option>
                        <option value="priceDesc">Price: High to Low</option>
                        <option value="rating">Highest Rated</option>
                        <option value="newest">Newest</option>
                    </select>
                </div>
            </div>
            
            <!-- View toggle buttons -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <p class="m-0">Showing <strong>24</strong> results</p>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-outline-primary active" id="gridView">
                        <i class="fas fa-th-large"></i>
                    </button>
                    <button type="button" class="btn btn-outline-primary" id="listView">
                        <i class="fas fa-list"></i>
                    </button>
                </div>
            </div>
            
            <!-- Vendor Cards Grid View -->
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4" id="vendorsGrid">
                <c:forEach begin="1" end="12">
                    <div class="col">
                        <div class="card h-100 border-0 shadow-sm vendor-card">
                            <div class="position-relative">
                                <img src="https://source.unsplash.com/random/300x200/?event,vendor,${status.index}" class="card-img-top" alt="Vendor Service">
                                <span class="position-absolute top-0 end-0 bg-primary text-white px-2 py-1 m-2 rounded-pill">
                                    <i class="fas fa-star"></i> 4.${status.index % 2 + 7}
                                </span>
                            </div>
                            <div class="card-body p-4">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="badge bg-secondary text-white">Category Name</span>
                                </div>
                                <h5 class="card-title mb-1">Vendor Service #${status.index}</h5>
                                <p class="text-muted small mb-3">by Vendor Company Name</p>
                                <p class="card-text">Professional service with customizable packages tailored for your specific needs.</p>
                                <div class="d-flex align-items-center mt-3">
                                    <i class="fas fa-map-marker-alt text-muted me-2"></i>
                                    <span class="text-muted small">Toronto, ON</span>
                                </div>
                            </div>
                            <div class="card-footer bg-white p-4 border-top-0">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="fw-bold text-primary">₹${(status.index + 1) * 100 - 10}.99</span>
                                    <a href="${pageContext.request.contextPath}/vendor/details/${status.index}" class="btn btn-outline-primary">
                                        View Details
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Vendor List View (Hidden by default) -->
            <div class="vendors-list d-none" id="vendorsList">
                <c:forEach begin="1" end="12">
                    <div class="card mb-3 border-0 shadow-sm">
                        <div class="row g-0">
                            <div class="col-md-4">
                                <img src="https://source.unsplash.com/random/300x200/?event,vendor,${status.index}" class="img-fluid rounded-start h-100 object-fit-cover" alt="Vendor Service">
                            </div>
                            <div class="col-md-8">
                                <div class="card-body p-4">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <span class="badge bg-secondary text-white">Category Name</span>
                                        <span class="text-warning">
                                            <i class="fas fa-star"></i> 4.${status.index % 2 + 7}
                                        </span>
                                    </div>
                                    <h5 class="card-title mb-1">Vendor Service #${status.index}</h5>
                                    <p class="text-muted small mb-3">by Vendor Company Name</p>
                                    <p class="card-text">Professional service with customizable packages tailored for your specific needs. We offer flexible solutions for events of all sizes.</p>
                                    <div class="d-flex align-items-center mt-3 mb-3">
                                        <i class="fas fa-map-marker-alt text-muted me-2"></i>
                                        <span class="text-muted small">Toronto, ON</span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="fw-bold text-primary">₹${(status.index + 1) * 100 - 10}.99</span>
                                        <a href="${pageContext.request.contextPath}/vendor/details/${status.index}" class="btn btn-outline-primary">
                                            View Details
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Pagination -->
            <nav class="mt-5">
                <ul class="pagination justify-content-center">
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                            <i class="fas fa-chevron-left"></i>
                        </a>
                    </li>
                    <li class="page-item active"><a class="page-link" href="#">1</a></li>
                    <li class="page-item"><a class="page-link" href="#">2</a></li>
                    <li class="page-item"><a class="page-link" href="#">3</a></li>
                    <li class="page-item">
                        <a class="page-link" href="#">
                            <i class="fas fa-chevron-right"></i>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>
</div>

<!-- Add JavaScript for filter functionality -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Price range slider
        const priceRange = document.getElementById('priceRange');
        const priceRangeValue = document.getElementById('priceRangeValue');
        
        if(priceRange && priceRangeValue) {
            priceRange.addEventListener('input', function() {
                priceRangeValue.textContent = '₹' + this.value + '+';
            });
        }
        
        // Reset filters button
        const resetFilters = document.getElementById('resetFilters');
        if(resetFilters) {
            resetFilters.addEventListener('click', function(e) {
                e.preventDefault();
                document.getElementById('filterForm').reset();
                priceRangeValue.textContent = '₹10000+';
                // Submit the form after resetting
                setTimeout(() => {
                    document.getElementById('filterForm').submit();
                }, 100);
            });
        }
        
        // View toggle functionality
        const gridView = document.getElementById('gridView');
        const listView = document.getElementById('listView');
        const vendorsGrid = document.getElementById('vendorsGrid');
        const vendorsList = document.getElementById('vendorsList');
        
        if(gridView && listView && vendorsGrid && vendorsList) {
            gridView.addEventListener('click', function() {
                gridView.classList.add('active');
                listView.classList.remove('active');
                vendorsGrid.classList.remove('d-none');
                vendorsList.classList.add('d-none');
            });
            
            listView.addEventListener('click', function() {
                listView.classList.add('active');
                gridView.classList.remove('active');
                vendorsList.classList.remove('d-none');
                vendorsGrid.classList.add('d-none');
            });
        }
        
        // Sort by change event
        const sortBy = document.getElementById('sortBy');
        if(sortBy) {
            sortBy.addEventListener('change', function() {
                // Add sorting parameter to the form and submit
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'sort';
                input.value = this.value;
                document.getElementById('filterForm').appendChild(input);
                document.getElementById('filterForm').submit();
            });
        }
    });
</script>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />