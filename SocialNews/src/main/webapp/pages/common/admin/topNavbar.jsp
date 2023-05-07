<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/navbar.css" type="text/css" media="screen">
<nav class="navbar bg-light">
    <div id="logo">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/homepage">
            <h1 class="fs-3">
                <!--img id="logo-img" src="${pageContext.request.contextPath}/images/icon.svg" alt="Logo"
                         class="d-inline-block align-text-middle img-fluid"-->
                <span>Social News - Administration page</span>
            </h1>
        </a>
    </div>
    <div class="d-flex flex-row align-items-center">
        <c:if test='${!param.page.equals("homepage")}'>
            <div class="d-flex flex-row align-items-center">
                <a class="icon-button" href="${pageContext.request.contextPath}/admin/homepage">
                    <i class="fs-3 bi bi-house-fill"></i>
                </a>
                <div class="icon-button dropdown ${param.page.equals("users") ? "focus":""}">
                    <span class="text-reset hidden-arrow dropdown-toggle" data-bs-toggle="dropdown">
                        <i class="fs-3 bi bi-people-fill"></i>
                    </span>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li>
                            <a class="dropdown-item"
                               href="${pageContext.request.contextPath}/admin/users?type=readers">
                                Readers
                            </a>
                            <a class="dropdown-item"
                               href="${pageContext.request.contextPath}/admin/users?type=reporters">
                                Reporters
                            </a>
                        </li>
                    </ul>
                </div>
                <a class="icon-button ${param.page.equals("addReporter") ? "focus":""}"
                   href="${pageContext.request.contextPath}/admin/addReporter">
                    <i class="fs-3 bi bi-person-plus-fill"></i>
                </a>
                <a class="icon-button ${param.page.equals("dashboard") ? "focus":""}"
                                       href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fs-3 bi bi-pie-chart-fill"></i>
                </a>
            </div>
        </c:if>
        <div id="user" class="icon-button dropdown">
            <span class="text-reset hidden-arrow dropdown-toggle" data-bs-toggle="dropdown">
                <i class="bi bi-person-fill-gear fs-3"></i>
            </span>
            <ul class="dropdown-menu dropdown-menu-end">
                <li>
                    <a id="logout" class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

