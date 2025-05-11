<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="User Profile" />
</jsp:include>

<div class="container py-5">
    <!-- Alert Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="row">
        <!-- Profile Sidebar -->
        <div class="col-md-3 mb-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="text-center mb-3">
                        <div class="avatar-circle mb-3 mx-auto">
                            <span class="avatar-initials">${user.name.charAt(0)}</span>
                        </div>
                        <h5 class="card-title mb-0">${user.name}</h5>
                        <p class="text-muted">${user.userType}</p>
                    </div>
                    
                    <ul class="nav flex-column nav-pills">
                        <li class="nav-item">
                            <a class="nav-link active" href="#profileSettings" data-bs-toggle="tab">
                                <i class="fas fa-user-edit me-2"></i>Profile Settings
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#passwordSettings" data-bs-toggle="tab">
                                <i class="fas fa-key me-2"></i>Change Password
                            </a>
                        </li>
                        <c:if test="${user.userType == 'CUSTOMER'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/user/bookings">
                                    <i class="fas fa-calendar-alt me-2"></i>My Bookings
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${user.userType == 'VENDOR'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/vendor-dashboard">
                                    <i class="fas fa-store me-2"></i>Vendor Dashboard
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
        
        <!-- Profile Content -->
        <div class="col-md-9">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <div class="tab-content">
                        <!-- Profile Settings Tab -->
                        <div class="tab-pane fade show active" id="profileSettings">
                            <h4 class="card-title mb-4">Profile Information</h4>
                            
                            <form action="${pageContext.request.contextPath}/user/profile" method="post">
                                <input type="hidden" name="action" value="updateProfile">
                                
                                <div class="mb-3">
                                    <label for="name" class="form-label">Full Name</label>
                                    <input type="text" class="form-control" id="name" name="name" value="${user.name}" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email Address</label>
                                    <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="phone" class="form-label">Phone Number</label>
                                    <input type="tel" class="form-control" id="phone" name="phone" value="${user.phone}" required>
                                </div>
                                
                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save me-2"></i>Save Changes
                                    </button>
                                </div>
                            </form>
                        </div>
                        
                        <!-- Password Settings Tab -->
                        <div class="tab-pane fade" id="passwordSettings">
                            <h4 class="card-title mb-4">Change Password</h4>
                            
                            <form action="${pageContext.request.contextPath}/user/profile" method="post">
                                <input type="hidden" name="action" value="updatePassword">
                                
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">Current Password</label>
                                    <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">New Password</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                                    <div class="form-text">Password must be at least 8 characters long and include letters and numbers</div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                </div>
                                
                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-key me-2"></i>Update Password
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .avatar-circle {
        width: 80px;
        height: 80px;
        background-color: #6c757d;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .avatar-initials {
        color: white;
        font-size: 32px;
        font-weight: bold;
        text-transform: uppercase;
    }
    .nav-pills .nav-link {
        color: #495057;
    }
    .nav-pills .nav-link.active {
        background-color: #0d6efd;
        color: #fff;
    }
</style>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />