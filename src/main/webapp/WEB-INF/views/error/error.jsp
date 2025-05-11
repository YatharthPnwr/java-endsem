<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Error" />
</jsp:include>
<div class="container py-5 text-center">
    <h1>An Error Occurred</h1>
    <c:if test="${not empty errorMessage}">
        <p class="alert alert-danger">${errorMessage}</p>
    </c:if>
    <c:if test="${not empty requestScope['javax.servlet.error.message']}">
         <p class="alert alert-danger">${requestScope['javax.servlet.error.message']}</p>
    </c:if>
     <c:if test="${not empty requestScope['javax.servlet.error.exception']}">
        <p>Details:</p>
        <pre style="text-align: left; background-color: #f8f9fa; padding: 10px; border: 1px solid #dee2e6; overflow-x: auto;">
            ${requestScope['javax.servlet.error.exception']}<br/>
            <c:forEach var="trace" items="${requestScope['javax.servlet.error.exception'].stackTrace}">    ${trace}<br/></c:forEach>
        </pre>
    </c:if>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">Go to Home</a>
</div>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />