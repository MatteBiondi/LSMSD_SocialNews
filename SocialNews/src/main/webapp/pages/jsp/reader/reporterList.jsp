<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="reporterList" scope="request" type="java.util.List"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reporterCard.css" type="text/css" media="screen">

<div id="reporter-items">
    <div class="row row-cols-2 row-cols-md-5 g-4">
        <c:forEach items="${reporterList}" var="reporter">
            <jsp:include page="reporterCard.jsp">
                <jsp:param name="image" value="${reporter.getPicture()}" />
                <jsp:param name="fullName" value="${reporter.getFullName()}" />
                <jsp:param name="id" value="${reporter.getId()}" />
            </jsp:include>
        </c:forEach>
    </div>
</div>