<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>SocialNews - Homepage</title>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/contentPage.css" type="text/css" media="screen">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.1.js"
            integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI="
            crossorigin="anonymous">
    </script>
    <script src="${pageContext.request.contextPath}/scripts/reader/homepage.js" type="module"></script>
</head>
<body>
    <!-- Navbar section -->
    <jsp:include page="/pages/common/navbar.jsp"/>

    <h1 class="content-title">Followed Reporters</h1>

    <!-- Pagination section -->
    <jsp:include page="/pages/common/pagination.jsp"/>

    <div class="d-flex justify-content-center" id="loading-spinner">
        <div class="spinner-grow" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>

    <div id="reporter_list">
        <!-- Filled by AJAX -->
    </div>
</body>
</html>