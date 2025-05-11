<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Your Cart" />
</jsp:include>

<div class="container py-5">
    <div class="row">
        <div class="col-lg-8">
            <h2 class="mb-4">Your Cart</h2>

            <c:choose>
                <c:when test="${empty cart || empty cart.vendorIds}">
                    <div class="alert alert-info">
                        <i class="fas fa-shopping-cart me-2"></i>Your cart is empty.
                        <a href="${pageContext.request.contextPath}/vendors" class="alert-link">Browse services</a> to add to your cart.
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Cart Items -->
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>Service</th>
                                    <th>Type</th>
                                    <th class="text-end">Price</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="vendor" items="${cartVendors}">
                                    <tr>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <div>
                                                    <h5 class="mb-0">${vendor.name}</h5>
                                                </div>
                                            </div>
                                        </td>
                                        <td><span class="badge bg-primary">${vendor.type}</span></td>
                                        <td class="text-end fw-bold">₹<fmt:formatNumber value="${vendor.baseCost}" pattern="#,##0.00"/></td>
                                        <td class="text-end">
                                            <form action="${pageContext.request.contextPath}/remove-from-cart" method="post">
                                                <input type="hidden" name="vendorId" value="${vendor.id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Remove from cart">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Recommendations section has been removed -->
                </c:otherwise>
            </c:choose>
        </div>

        <div class="col-lg-4 mt-4 mt-lg-0">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h4 class="card-title mb-0">Order Summary</h4>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-2">
                        <span>Services:</span>
                        <span>${cart.itemCount}</span>
                    </div>

                    <hr>

                    <div class="d-flex justify-content-between mb-3">
                        <h5>Total:</h5>
                        <h5>₹<fmt:formatNumber value="${cart.totalCost}" pattern="#,##0.00"/></h5>
                    </div>

                    <div class="d-grid gap-2">
                        <c:choose>
                            <c:when test="${empty cart || empty cart.vendorIds}">
                                <a href="${pageContext.request.contextPath}/vendors" class="btn btn-primary">Browse Services</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">Proceed to Checkout</a>
                                <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-secondary">Continue Shopping</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- Need Help Card -->
            <div class="card mt-4">
                <div class="card-body">
                    <h5><i class="fas fa-question-circle me-2"></i>Need Help?</h5>
                    <p class="card-text">If you have questions about these services or need assistance with your booking.</p>
                    <div class="d-grid">
                        <button class="btn btn-outline-primary">Contact Support</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />