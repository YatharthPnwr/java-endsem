<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="${formAction == 'edit' ? 'Edit Service' : 'Add New Service'}" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow">
                <div class="card-header">
                    <h2 class="card-title text-center mb-0">${formAction == 'edit' ? 'Edit Service' : 'Add New Service'} for ${vendor.name}</h2>
                </div>
                <div class="card-body p-4">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            ${errorMessage}
                        </div>
                    </c:if>
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success" role="alert">
                            ${successMessage}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/vendor/service/save" method="post">
                        <!-- Hidden field for service ID if editing -->
                        <c:if test="${formAction == 'edit' && not empty vendorService.id}">
                            <input type="hidden" name="id" value="${vendorService.id}">
                        </c:if>
                        
                        <input type="hidden" name="vendorId" value="${vendor.id}"> <%-- Should be implicitly set via session/vendor object in servlet --%>

                        <div class="mb-3">
                            <label for="name" class="form-label">Service Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" value="${vendorService.name}" required>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="3">${vendorService.description}</textarea>
                        </div>

                        <div class="mb-3">
                            <label for="serviceType" class="form-label">Service Type <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="serviceType" name="serviceType" value="${not empty vendorService.serviceType ? vendorService.serviceType : vendor.type}" required>
                            <small class="form-text text-muted">E.g., Full Package, Basic Setup, Ala Carte Item. This can be more specific than your main vendor type.</small>
                        </div>

                        <div class="mb-3">
                            <label for="price" class="form-label">Price (₹) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="price" name="price" value="${vendorService.price}" required>
                        </div>

                        <div class="mb-3">
                            <label for="location" class="form-label">Service Location (if different from main)</label>
                            <input type="text" class="form-control" id="location" name="location" value="${not empty vendorService.location ? vendorService.location : vendor.location}">
                        </div>

                        <div class="mb-3">
                            <label for="imageUrl" class="form-label">Image URL (Optional)</label>
                            <input type="url" class="form-control" id="imageUrl" name="imageUrl" value="${vendorService.imageUrl}">
                        </div>
                        
                        <%-- 
                        <div class="mb-3 form-check">
                            <input type="checkbox" class="form-check-input" id="isActive" name="isActive" value="true" ${ (empty vendorService || vendorService.active) ? 'checked' : '' }>
                            <label class="form-check-label" for="isActive">Service is Active</label>
                        </div>
                        --%>


                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/vendor-dashboard" class="btn btn-outline-secondary">Cancel</a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i> ${formAction == 'edit' ? 'Update Service' : 'Add Service'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />