<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Page Not Found" />
</jsp:include>
<div class="container py-5 text-center">
    <h1>404 - Page Not Found</h1>
    <p>Sorry, the page you are looking for does not exist.</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Home</a>
</div>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />