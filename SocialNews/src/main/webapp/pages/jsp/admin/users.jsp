<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="pageSize" scope="request" type="java.lang.Integer"/>
<c:set
        var="baseURL"
        value="${fn:replace(
            pageContext.request.requestURL,
            fn:substring(pageContext.request.requestURI, 0, fn:length(pageContext.request.requestURI)),
            pageContext.request.contextPath
        )}"
/>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/admin/users.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <title>SocialNews</title>
</head>

<body>

<header id="header">
    <jsp:include page="../../common/admin/topNavbar.jsp" >
        <jsp:param name="page" value="users" />
    </jsp:include>
</header>

<section id="content">
    <zing-grid id="users-table" caption="Registered ${param.type}" search page-size="${pageSize}" align="center"
               zebra>
        <zg-data>
            <zg-param name="src"
                      value="${baseURL}/admin/users?type=${param.type}&data=true">
            </zg-param>
            <zg-param name="recordPath" value="users"></zg-param>
            <!-- PAGING -->
            <zg-param name="loadByPage" value="true"></zg-param>
            <zg-param name="prevIDPath" value="prev"></zg-param>
            <zg-param name="nextIDPath" value="next"></zg-param>
            <zg-param name="prevIDKey" value="prevOffset"></zg-param>
            <zg-param name="nextIDKey" value="nextOffset"></zg-param>
            <!-- SEARCH -->
            <zg-param name="searchKey" value="search"></zg-param>
            <!-- DELETE -->
            <zg-param name="deleteOptions"
                      value='{"method":"DELETE", "src":"${baseURL}/admin/users", "queryString":"?type=${param.type}"}'>
            </zg-param>
        </zg-data>
        <c:choose>
            <c:when test='${param.type.equals("readers")}'>
                <zg-colgroup>
                    <zg-column index="email"></zg-column>
                    <zg-column index="fullName"></zg-column>
                    <zg-column index="gender"></zg-column>
                    <zg-column index="country"></zg-column>
                    <zg-column type="remover" header="Remove User"></zg-column>
                </zg-colgroup>
            </c:when>
            <c:otherwise>
                <zg-colgroup>
                    <zg-column index="email"></zg-column>
                    <zg-column index="fullName"></zg-column>
                    <zg-column index="gender"></zg-column>
                    <zg-column index="location"></zg-column>
                    <zg-column index="dateOfBirth"></zg-column>
                    <zg-column index="cell"></zg-column>
                    <zg-column type="remover" header="Remove User"></zg-column>
                    <!-- TODO: Reports -->
                </zg-colgroup>
            </c:otherwise>
        </c:choose>
        <zg-footer>
            <zg-pager>
                <zg-button slot="left" action="prevpage"></zg-button>
                <zg-text slot="center" value="currpage"></zg-text>
                <zg-button slot="right" action="nextpage"></zg-button>
            </zg-pager>
        </zg-footer>
    </zing-grid>
</section>
</body>
</html>
