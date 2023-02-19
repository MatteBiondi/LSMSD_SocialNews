<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="userType" value="${sessionScope.userType}"/>
<c:set var="userID" value="${sessionScope.id}"/>

<jsp:useBean id="reporterID" scope="request" type="java.lang.String"/>
<jsp:useBean id="fullName" scope="request" type="java.lang.String"/>
<jsp:useBean id="followers" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="location" scope="request" type="java.lang.String"/>
<jsp:useBean id="dateOfBirth" scope="request" type="java.util.Date"/>
<jsp:useBean id="cell" scope="request" type="java.lang.String"/>
<jsp:useBean id="email" scope="request" type="java.lang.String"/>
<jsp:useBean id="picture" scope="request" type="java.lang.String"/>
<jsp:useBean id="postsList" scope="request" type="java.util.List"/>

<!DOCTYPE html>
<html>
<head>
    <title>Social News - Reporter Page</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reporterHomepage.css" type="text/css" media="screen">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" type="text/css" media="screen">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/postCard.css" type="text/css" media="screen">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.1.js" integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI=" crossorigin="anonymous"></script>
    <script src="${pageContext.request.contextPath}/scripts/reporter/homepage.js"></script>
</head>
<body>
    <!-- Navbar section -->
    <jsp:include page="../../common/navbar.jsp"/>

    <header>
        <div id="reporter">
            <div id="main-info" class="row header">
                <div class="col-sm-3">
                    <img src="${picture}" class="rounded-circle profile-image" alt="Profile Picture">
                    <div class="col-sm-9">
                        <h4 id="reporter-name">${fullName}</h4>
                        <h2 id="role">Reporter</h2>
                        <c:if test="${userType==\"reader\"}">
                            <!-- todo: gestire il caso di follow-unfollow-->
                            <button id="follow-button" type="button" class="btn btn-outline-primary">Follow</button>
                        </c:if>
                    </div>
                </div>
                <div id="followers-col" class="col-sm-4 reporter-followers">
                    <h2>Followers</h2>
                    <p id="followers-number">${followers}</p>
                </div>
            </div>
            <div class="info-div row header">
                <div class="info-and-contacs col-sm-12">
                    <h2>Info & Contacts</h2>
                    <p><fmt:formatDate value="${dateOfBirth}" pattern="dd/MM/yyyy" /></p>
                    <p>${location}</p>
                    <div id="contact-info">
                        <p>${cell}</p>
                        <p>${email}</p>
                    </div>
                </div>
            </div>
        </div>

    </header>
    <div id="new-post-div" class="post-container container my-5">
        <h2 id="add-post-title">Write a new post</h2>
        <div class="form-group">
            <textarea class="form-control" id="message" rows="3" placeholder="Enter post text here"></textarea>
            <input id="hashtags-input" type="text" class="form-control" placeholder="Enter content hashtags (without the # symbol and separated by space)">
            <input id="related-links-input" type="text" class="form-control" placeholder="Enter related links (separated by space)">
        </div>
        <button id="write-post" onclick="publishNewPost('${userID}')">Publish</button>
    </div>

    <jsp:include page="../postList.jsp"/>

    <!-- todo: paging for posts -->
</body>
</html>
