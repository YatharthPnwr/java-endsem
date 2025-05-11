<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Login" />
</jsp:include>

<style>
    .demo-credentials-card {
        border-left: 5px solid #0d6efd; /* Bootstrap primary blue */
        background-color: #f8f9fa; /* Light grey background */
        height: 100%; /* Make card take full height of its column if needed */
    }
    .demo-credentials-card .card-header {
        background-color: #e9ecef; /* Slightly darker grey for header */
        border-bottom: 1px solid #ced4da;
    }
    .demo-credentials-card .list-group-item {
        background-color: transparent;
        border: none;
        padding-left: 0;
        padding-right: 0;
    }
    .demo-credentials-card .badge {
        font-size: 0.8rem;
    }
    /* Ensure cards in the same row have similar perceived height or alignment */
    .login-row > .col-md-7, .login-row > .col-md-5 {
        display: flex;
        flex-direction: column;
    }
    .login-row > .col-md-7 > .card, .login-row > .col-md-5 > .card {
        flex-grow: 1;
    }
</style>

<div class="container py-5">
    <div class="row login-row"> <!-- Removed justify-content-center to use full width potential -->
        <!-- Login Component Column -->
        <div class="col-md-7 mb-4 mb-md-0">
            <div class="card shadow h-100">
                <div class="card-body p-4 p-lg-5">
                    <h2 class="card-title text-center mb-4">Login</h2>
                    
                    <!-- Display error messages if any -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.message}">
                        <div class="alert alert-info alert-dismissible fade show" role="alert">
                            <i class="fas fa-info-circle me-2"></i>${requestScope.message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <div class="mb-3">
                            <label for="email" class="form-label">Email address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                <input type="email" class="form-control" id="email" name="email" required>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                <input type="password" class="form-control" id="password" name="password" required>
                            </div>
                        </div>
                        
                        <div class="mb-3 form-check">
                            <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                            <label class="form-check-label" for="rememberMe">Remember me</label>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">Login</button>
                        </div>
                    </form>
                    
                    <div class="text-center mt-4">
                        <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register now</a></p>
                        <p><a href="#" class="text-decoration-none">Forgot password?</a></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Demo Login Credentials Column -->
        <div class="col-md-5">
            <div class="card demo-credentials-card">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-info-circle text-primary me-2"></i>For Demo Purposes
                    </h5>
                </div>
                <div class="card-body">
                    <p class="card-text">You can use the following credentials to explore the application:</p>
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item py-2">
                            <span class="badge bg-success me-2">CUSTOMER</span>
                            <div>
                                <strong>Email:</strong> <code>rakesh@example.com</code><br>
                                <strong>Password:</strong> <code>password123</code>
                            </div>
                        </li>
                        <li class="list-group-item py-2">
                            <span class="badge bg-warning text-dark me-2">VENDOR</span>
                            <div>
                                <strong>Email:</strong> <code>info@elegantvenuesinc.com</code><br>
                                <strong>Password:</strong> <code>vendor123</code>
                            </div>
                        </li>
                    </ul>
                    <small class="d-block text-muted mt-3">
                        These are pre-populated accounts. You can also <a href="${pageContext.request.contextPath}/register">register your own</a>.
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />