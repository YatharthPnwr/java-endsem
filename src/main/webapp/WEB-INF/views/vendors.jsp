<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
                            </div>
                        </div>

                        <!-- Service Type Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">Service Type</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="type" id="allTypes" value="" 
                                       ${empty param.type ? 'checked' : ''}>
                                <label class="form-check-label" for="allTypes">All Types</label>
                            </div>
                            <c:forEach var="type" items="${vendorTypes}">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="type" 
                                           id="type${type}" value="${type}" 
                                           ${param.type eq type ? 'checked' : ''}>
                                    <label class="form-check-label" for="type${type}">${type}</label>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- Price Range Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">Price Range (₹)</label>
                            <div class="row g-2">
                                <div class="col-6">
                                    <input type="number" class="form-control form-control-sm" 
                                           placeholder="Min" name="minPrice" value="${param.minPrice}">
                                </div>
                                <div class="col-6">
                                    <input type="number" class="form-control form-control-sm" 
                                           placeholder="Max" name="maxPrice" value="${param.maxPrice}">
                                </div>
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Apply Filters</button>
                            <button type="button" class="btn btn-outline-secondary" id="resetFilters">Reset Filters</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Vendors Listing -->
        <div class="col-lg-9">
            <c:choose>
                <c:when test="${empty vendors}">
                    <div class="alert alert-info" role="alert">
                        <i class="fas fa-info-circle me-2"></i>No vendors found matching your criteria.
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Results count and sort options -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <p class="mb-0 text-muted"><strong>${vendors.size()}</strong> services found</p>
                        <div class="dropdown">
                            <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                Sort By: ${empty currentSort ? 'Default' : currentSort eq 'price_asc' ? 'Price: Low to High' : currentSort eq 'price_desc' ? 'Price: High to Low' : currentSort eq 'name_asc' ? 'Name: A to Z' : currentSort eq 'name_desc' ? 'Name: Z to A' : 'Default'}
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item ${empty currentSort ? 'active' : ''}" href="?${pageContext.request.queryString.replaceAll('&?sort=[^&]*', '')}">Default</a></li>
                                <li><a class="dropdown-item ${currentSort eq 'price_asc' ? 'active' : ''}" href="?${pageContext.request.queryString.replaceAll('&?sort=[^&]*', '')}&sort=price_asc">Price: Low to High</a></li>
                                <li><a class="dropdown-item ${currentSort eq 'price_desc' ? 'active' : ''}" href="?${pageContext.request.queryString.replaceAll('&?sort=[^&]*', '')}&sort=price_desc">Price: High to Low</a></li>
                                <li><a class="dropdown-item ${currentSort eq 'name_asc' ? 'active' : ''}" href="?${pageContext.request.queryString.replaceAll('&?sort=[^&]*', '')}&sort=name_asc">Name: A to Z</a></li>
                                <li><a class="dropdown-item ${currentSort eq 'name_desc' ? 'active' : ''}" href="?${pageContext.request.queryString.replaceAll('&?sort=[^&]*', '')}&sort=name_desc">Name: Z to A</a></li>
                            </ul>
                        </div>
                    </div>

                    <!-- Vendor Cards -->
                    <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
                        <c:forEach var="vendor" items="${vendors}">
                            <div class="col">
                                <div class="card vendor-card h-100 shadow-sm">
                                    <c:if test="${not empty vendor.imageUrl}">
                                        <img src="${vendor.imageUrl}" class="card-img-top" alt="${vendor.name}" style="height: 200px; object-fit: cover;">
                                    </c:if>
                                    <div class="card-body">
                                        <h5 class="card-title">${vendor.name}</h5>
                                        <p class="badge bg-primary mb-2">${vendor.type}</p>
                                        <p class="card-text text-muted small">${fn:length(vendor.description) > 100 ? fn:substring(vendor.description, 0, 100).concat('...') : vendor.description}</p>
                                        <div class="d-flex justify-content-between align-items-center mt-3">
                                            <span class="fw-bold text-primary">₹${vendor.baseCost}</span>
                                            <button type="button" class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#vendorModal${vendor.id}">
                                                <i class="fas fa-info-circle me-1"></i> Details
                                            </button>
                                        </div>
                                    </div>
                                    <div class="card-footer bg-white border-top pt-3">
                                        <c:if test="${sessionScope.user != null}">
                                            <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                                                <input type="hidden" name="vendorId" value="${vendor.id}">
                                                <button type="submit" class="btn btn-primary w-100">
                                                    <i class="fas fa-cart-plus me-2"></i>Add to Cart
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${sessionScope.user == null}">
                                            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary w-100">
                                                <i class="fas fa-sign-in-alt me-2"></i>Login to Book
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <!-- Modal for this vendor -->
                            <div class="modal fade" id="vendorModal${vendor.id}" tabindex="-1" aria-labelledby="vendorModalLabel${vendor.id}" aria-hidden="true">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="vendorModalLabel${vendor.id}">${vendor.name}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <img src="${not empty vendor.imageUrl ? vendor.imageUrl : 'https://via.placeholder.com/400x300?text=No+Image'}" 
                                                         class="img-fluid rounded" alt="${vendor.name}" style="max-height: 300px; width: 100%; object-fit: cover;">
                                                </div>
                                                <div class="col-md-6">
                                                    <h4>${vendor.name}</h4>
                                                    <span class="badge bg-primary mb-2">${vendor.type}</span>
                                                    <div class="d-flex text-warning mb-2">
                                                        <i class="fas fa-star"></i>
                                                        <i class="fas fa-star"></i>
                                                        <i class="fas fa-star"></i>
                                                        <i class="fas fa-star"></i>
                                                        <i class="fas fa-star-half-alt"></i>
                                                        <span class="text-dark ms-2">(${vendor.rating})</span>
                                                    </div>
                                                    <h5 class="text-primary mb-3">₹${vendor.baseCost}</h5>
                                                    <p>${vendor.description}</p>
                                                    <h6>Contact Information:</h6>
                                                    <p>${vendor.contactInfo}</p>
                                                    <p><strong>Location:</strong> ${vendor.location}</p>

                                                    <div class="mt-4">
                                                        <c:if test="${sessionScope.user != null}">
                                                            <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                                                                <input type="hidden" name="vendorId" value="${vendor.id}">
                                                                <button type="submit" class="btn btn-primary w-100">
                                                                    <i class="fas fa-cart-plus me-2"></i>Add to Cart
                                                                </button>
                                                            </form>
                                                        </c:if>
                                                        <c:if test="${sessionScope.user == null}">
                                                            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary w-100">
                                                                <i class="fas fa-sign-in-alt me-2"></i>Login to Book
                                                            </a>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
    // Reset filters button functionality
    document.getElementById('resetFilters').addEventListener('click', function () {
        window.location.href = '${pageContext.request.contextPath}/vendors';
    });
</script>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />