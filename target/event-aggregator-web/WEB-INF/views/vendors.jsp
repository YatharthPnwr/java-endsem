<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Browse Services" />
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
                            <label for="searchQuery" class="form-label fw-bold">Search Services / Vendors</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="searchQuery" name="query" 
                                    value="${param.query}" placeholder="Service name, vendor...">
                            </div>
                        </div>
                        
                        <!-- Service Type Filter -->
                        <div class="mb-4">
                            <label class="form-label fw-bold">Service Type</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="type" id="allTypes" value="" 
                                       ${empty param.type ? 'checked' : ''} onchange="this.form.submit()">
                                <label class="form-check-label" for="allTypes">All Types</label>
                            </div>
                            <c:forEach var="serviceTypeName" items="${vendorTypes}"> 
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="type" 
                                           id="type${fn:replace(serviceTypeName, ' ', '')}" value="${serviceTypeName}" 
                                           ${param.type eq serviceTypeName ? 'checked' : ''} onchange="this.form.submit()">
                                    <label class="form-check-label" for="type${fn:replace(serviceTypeName, ' ', '')}">${serviceTypeName}</label>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Apply Filters</button>
                            <button type="button" class="btn btn-outline-secondary" id="resetFilters">Reset Filters</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <!-- Services Listing -->
        <div class="col-lg-9">
             <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Available Services</h2>
                <div class="dropdown">
                    <button class="btn btn-outline-secondary dropdown-toggle btn-sm" type="button" id="sortDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        Sort By: ${empty currentSort ? 'Default' : currentSort eq 'price_asc' ? 'Price: Low to High' : currentSort eq 'price_desc' ? 'Price: High to Low' : currentSort eq 'name_asc' ? 'Service Name: A-Z' : currentSort eq 'name_desc' ? 'Service Name: Z-A' : 'Default'}
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="sortDropdown">
                        <li><a class="dropdown-item ${empty currentSort || currentSort eq 'name_asc' ? 'active' : ''}" href="#" data-sort="name_asc">Service Name: A-Z</a></li>
                        <li><a class="dropdown-item ${currentSort eq 'name_desc' ? 'active' : ''}" href="#" data-sort="name_desc">Service Name: Z-A</a></li>
                        <li><a class="dropdown-item ${currentSort eq 'price_asc' ? 'active' : ''}" href="#" data-sort="price_asc">Price: Low to High</a></li>
                        <li><a class="dropdown-item ${currentSort eq 'price_desc' ? 'active' : ''}" href="#" data-sort="price_desc">Price: High to Low</a></li>
                    </ul>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty vendorServiceList}">
                    <div class="alert alert-info" role="alert">
                        <i class="fas fa-info-circle me-2"></i>No services found matching your criteria.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                        <c:forEach var="serviceItem" items="${vendorServiceList}" varStatus="loopStatus">
                            <div class="col">
                                <div class="card vendor-card h-100 shadow-sm">
                                    <c:set var="displayImageUrl" value="https://via.placeholder.com/300x200?text=Service"/>
                                    <c:if test="${not empty serviceItem.imageUrl}">
                                        <c:set var="displayImageUrl" value="${serviceItem.imageUrl}"/>
                                    </c:if>
                                    <img src="${displayImageUrl}" class="card-img-top" alt="${serviceItem.name}" style="height: 200px; object-fit: cover;">
                                    
                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title">${serviceItem.name}</h5>
                                        <p class="badge bg-info text-dark mb-1">${serviceItem.serviceType}</p>
                                        <p class="text-muted small">
                                            By: <a href="${pageContext.request.contextPath}/vendor/${serviceItem.vendor.id}">${serviceItem.vendor.name}</a> (${serviceItem.vendor.type})
                                        </p>
                                        
                                        <p class="card-text text-muted small flex-grow-1">
                                            ${fn:length(serviceItem.description) > 70 ? fn:substring(serviceItem.description, 0, 70).concat('...') : serviceItem.description}
                                        </p>
                                        <div class="d-flex justify-content-between align-items-center mt-2">
                                            <span class="fw-bold text-primary fs-5">₹<fmt:formatNumber value="${serviceItem.price}" pattern="#,##0.00"/></span>
                                            <!-- MODAL TRIGGER BUTTON -->
                                            <button type="button" class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#serviceModal${serviceItem.id}">
                                                <i class="fas fa-info-circle me-1"></i> Details
                                            </button>
                                        </div>
                                    </div>
                                    <div class="card-footer bg-white border-top-0 pt-3">
                                        <c:if test="${sessionScope.user != null}">
                                            <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                                                <input type="hidden" name="vendorId" value="${serviceItem.vendor.id}">
                                                <button type="submit" class="btn btn-primary w-100">
                                                    <i class="fas fa-cart-plus me-2"></i>Add ${serviceItem.vendor.name}
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

                            <!-- MODAL FOR THIS SERVICE ITEM -->
                            <div class="modal fade" id="serviceModal${serviceItem.id}" tabindex="-1" aria-labelledby="serviceModalLabel${serviceItem.id}" aria-hidden="true">
                                <div class="modal-dialog modal-lg modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="serviceModalLabel${serviceItem.id}">${serviceItem.name}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="row">
                                                <div class="col-md-5">
                                                    <img src="${not empty serviceItem.imageUrl ? serviceItem.imageUrl : (not empty serviceItem.vendor.imageUrl ? serviceItem.vendor.imageUrl : 'https://via.placeholder.com/400x300?text=Service')}" 
                                                         class="img-fluid rounded mb-3" alt="${serviceItem.name}" style="max-height: 300px; width: 100%; object-fit: cover;">
                                                </div>
                                                <div class="col-md-7">
                                                    <h4>${serviceItem.name}</h4>
                                                    <p><span class="badge bg-info text-dark">${serviceItem.serviceType}</span></p>
                                                    <p class="text-muted">Offered by: <strong><a href="${pageContext.request.contextPath}/vendor/${serviceItem.vendor.id}">${serviceItem.vendor.name}</a></strong> (${serviceItem.vendor.type})</p>
                                                    
                                                    <h5 class="text-primary mb-3">Price: ₹<fmt:formatNumber value="${serviceItem.price}" pattern="#,##0.00"/></h5>
                                                    
                                                    <h6>Description:</h6>
                                                    <p>${serviceItem.description}</p>
                                                    
                                                    <c:if test="${not empty serviceItem.location}">
                                                        <p><strong>Service Location:</strong> ${serviceItem.location}</p>
                                                    </c:if>
                                                    <p><strong>Vendor Main Location:</strong> ${serviceItem.vendor.location}</p>

                                                    <div class="mt-3">
                                                        <c:if test="${sessionScope.user != null}">
                                                            <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                                                                <input type="hidden" name="vendorId" value="${serviceItem.vendor.id}">
                                                                <button type="submit" class="btn btn-primary w-100">
                                                                    <i class="fas fa-cart-plus me-2"></i>Add ${serviceItem.vendor.name} to Cart
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
                                            <a href="${pageContext.request.contextPath}/vendor/${serviceItem.vendor.id}" class="btn btn-outline-secondary">View Full Vendor Profile</a>
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
    document.addEventListener('DOMContentLoaded', function() {
        const resetFiltersBtn = document.getElementById('resetFilters');
        if(resetFiltersBtn) {
            resetFiltersBtn.addEventListener('click', function() {
                const form = document.getElementById('filterForm');
                form.reset(); 
                const allTypesRadio = document.getElementById('allTypes');
                if (allTypesRadio) allTypesRadio.checked = true;
                
                const searchQueryInput = document.getElementById('searchQuery');
                if (searchQueryInput) searchQueryInput.value = '';

                let existingSortInput = form.querySelector('input[name="sort"]');
                if (existingSortInput) {
                    existingSortInput.remove();
                }
                form.submit();
            });
        }
        
        const sortDropdownItems = document.querySelectorAll('#sortDropdown + .dropdown-menu .dropdown-item');
        sortDropdownItems.forEach(item => {
            item.addEventListener('click', function(e) {
                e.preventDefault();
                const sortByValue = this.dataset.sort;
                const form = document.getElementById('filterForm');
                
                let sortInput = form.querySelector('input[name="sort"]');
                if (!sortInput) {
                    sortInput = document.createElement('input');
                    sortInput.type = 'hidden';
                    sortInput.name = 'sort';
                    form.appendChild(sortInput);
                }
                sortInput.value = sortByValue === 'name_asc' ? '' : sortByValue; // Default to name_asc (no param)
                form.submit();
            });
        });

        // Set active state for sort dropdown button text based on currentSort attribute
        const sortDropdownButton = document.getElementById('sortDropdown');
        const currentSortValue = "<c:out value='${currentSort}' default='' />";
        if (sortDropdownButton) {
            let sortText = "Sort By: Default";
            if (currentSortValue === 'price_asc') sortText = "Sort By: Price: Low to High";
            else if (currentSortValue === 'price_desc') sortText = "Sort By: Price: High to Low";
            else if (currentSortValue === 'name_asc' || currentSortValue === '') sortText = "Sort By: Service Name: A-Z";
            else if (currentSortValue === 'name_desc') sortText = "Sort By: Service Name: Z-A";
            sortDropdownButton.textContent = sortText;
        }
    });
</script>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />