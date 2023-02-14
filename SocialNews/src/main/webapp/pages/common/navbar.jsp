<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" type="text/css" media="screen">
<script src="${pageContext.request.contextPath}/scripts/navbar.js"></script>
<script src="https://code.jquery.com/jquery-3.6.1.js"
        integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI="
        crossorigin="anonymous">
</script>

<nav class="navbar-top navbar navbar-light bg-light">
    <div id="logo">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/${sessionScope.userType}/homepage">
            <h1 class="fs-3">
                <!--img id="logo-img" src="${pageContext.request.contextPath}/images/icon.svg" alt="Logo"
                         class="d-inline-block align-text-middle img-fluid"-->
                <span>Social News</span>
            </h1>
        </a>
    </div>
    <c:if test="${ sessionScope.userType == \"reader\" }">
        <form method="GET">
            <div class="d-flex align-items-center" id="search">
                <div class="input-group">
                    <button id="search-button" type="button" class="btn btn-primary">Search by</button>
                    <input id="search-text" type="text" class="form-control" name="value" required>
                    <div class="btn-group">
                        <button id="search-chooser" type="button" data-bs-toggle="dropdown"
                                class="btn btn-primary dropdown-toggle dropdown-toggle-split"></button>
                        <i id="search-clear" class="bi bi-x-circle"></i>
                        <div class="dropdown-menu">
                            <span class="dropdown-item search-item">Reporter Name</span>
                            <span class="dropdown-item search-item">Keyword</span>
                        </div>
                    </div>
                </div>
            </div>
        </form>

    </c:if>
    <div class="d-flex flex-row align-items-center" id="icons">
        <div class="d-flex flex-row align-items-center">
            <a class="icon-button" href="${pageContext.request.contextPath}/${sessionScope.userType}/homepage">
                <i class="fs-3 bi bi-house"></i>
            </a>
            <a class="icon-button" href="${pageContext.request.contextPath}/${sessionScope.userType}/statistics">
                <i class="fs-3 bi bi-bar-chart"></i>
            </a>
        </div>

        <div id="user" class="icon-button dropdown">
            <span class="text-reset hidden-arrow" data-bs-toggle="dropdown">
                <i class="bi bi-gear fs-3"></i>
            </span>
            <ul class="dropdown-menu dropdown-menu-end">
                <c:if test="${ sessionScope.userType == \"reader\" }">
                    <li>
                        <a id="profile" class="dropdown-item" href="${pageContext.request.contextPath}/${sessionScope.userType}/profile">My Profile</a>
                    </li>
                </c:if>
                <li>
                    <a id="logout" class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

