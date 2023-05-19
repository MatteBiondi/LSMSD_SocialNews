<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="userType" value="${sessionScope.userType}"/>
<c:set var="userID" value="${sessionScope.id}"/>

<jsp:useBean id="reporterPage" scope="request" type="it.unipi.lsmsd.socialnews.dto.ReporterPageDTO"/>
<jsp:useBean id="postsList" scope="request" type="java.util.List"/>


<!DOCTYPE html>
<html>
<head>
    <title>Social News - Reporter Page</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reporterHomepage.css" type="text/css" media="screen">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/postCard.css" type="text/css" media="screen">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/comment.css" type="text/css" media="screen">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.1.js" integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI=" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/reporter/homepage.js" type="module"></script>
</head>
<body data-user-id="${userID}" data-user-type="${userType}" data-reporter-id="${reporterPage.reporter.id}" data-is-follower="${reporterPage.isFollower}">
    <!-- Navbar section -->
    <c:choose>
        <c:when test="${userType==\"admin\"}">
            <jsp:include page="/pages/common/admin/topNavbar.jsp">
                <jsp:param name="page" value="users" />
            </jsp:include>
        </c:when>
        <c:otherwise>
            <jsp:include page="/pages/common/navbar.jsp"/>
        </c:otherwise>
    </c:choose>

    <header>
        <div id="reporter">
            <div id="main-info" class="row header">
                <div class="col-sm-3">
                    <img src="${reporterPage.reporter.picture}" class="rounded-circle profile-image" alt="Profile Picture">
                    <div class="col-sm-9">
                        <h4 id="reporter-name">${reporterPage.reporter.fullName}</h4>
                        <h2 id="role">Reporter</h2>
                        <c:if test="${userType==\"reader\"}">
                            <button id="follow-button" type="button" class="btn btn-outline-primary">Follow</button>
                            <button id="unfollow-button" type="button" class="btn btn-outline-primary">Unfollow</button>
                        </c:if>
                    </div>
                </div>
                <div id="followers-col" class="col-sm-4 reporter-followers">
                    <h2>Followers</h2>
                    <p id="followers-number">${reporterPage.numOfFollower}</p>
                </div>
            </div>
            <div class="info-div row header">
                <div class="info-and-contacs col-sm-12">
                    <h2>Info & Contacts</h2>
                    <p><fmt:formatDate value="${reporterPage.reporter.dateOfBirth}" pattern="dd/MM/yyyy" /></p>
                    <p>${reporterPage.reporter.location}</p>
                    <div id="contact-info">
                        <p>${reporterPage.reporter.cell}</p>
                        <p>${reporterPage.reporter.email}</p>
                    </div>
                </div>
            </div>
        </div>

    </header>
    <c:if test="${userType==\"reporter\"}">
        <div id="new-post-div" class="post-container container my-5">
            <h2 id="add-post-title">Write a new post</h2>
            <div class="form-group">
                <label for="new-post-textarea"></label><textarea class="form-control" id="new-post-textarea" rows="3" placeholder="Enter post text here"></textarea>
                <label for="hashtags-input"></label><input id="hashtags-input" type="text" class="form-control" placeholder="Enter content hashtags (without the # symbol and separated by space)">
                <label for="related-links-input"></label><input id="related-links-input" type="text" class="form-control" placeholder="Enter related links (separated by space)">
            </div>
            <button id="write-post">Publish</button>
        </div>
    </c:if>

    <jsp:include page="/pages/jsp/postList.jsp"/>

    <!-- Pagination section -->
    <jsp:include page="/pages/common/pagination.jsp"/>
</body>
</html>
