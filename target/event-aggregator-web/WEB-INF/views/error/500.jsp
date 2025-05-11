<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/includes/header.jsp">
    <jsp:param name="pageTitle" value="Server Error" />
</jsp:include>
<div class="container py-5 text-center">
    <h1>500 - Internal Server Error</h1>
    <p>Sorry, something went wrong on our side. Please try again later.</p>
    <c:if test="${not empty requestScope['javax.servlet.error.exception']}">
        <pre style="text-align: left; background-color: #f8f9fa; padding: 10px; border: 1px solid #dee2e6; overflow-x: auto;">
            Error: ${requestScope['javax.servlet.error.exception']}<br/>
            <c:forEach var="trace" items="${requestScope['javax.servlet.error.exception'].stackTrace}">    ${trace}<br/></c:forEach>
        </pre>
    </c:if>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Home</a>
</div>
<jsp:include page="/WEB-INF/views/includes/footer.jsp" />