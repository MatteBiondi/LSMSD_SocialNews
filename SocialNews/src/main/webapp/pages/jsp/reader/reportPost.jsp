<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Social News - Report Post</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/contentPage.css" type="text/css" media="screen">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reportPost.css" type="text/css" media="screen">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.1.js"
            integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI="
            crossorigin="anonymous">
    </script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/reader/reportPost.js"></script>
</head>
<body>
    <!-- Navbar section -->
    <jsp:include page="../../common/navbar.jsp"/>

    <h1 class="content-title">Report Post</h1>
    <div class="form-container">
        <header class="alert-text">
            <p>
                Welcome to our content reporting page! We take inappropriate content very seriously and appreciate your efforts
                to help keep our platform safe and enjoyable for everyone. If you come across a post with inappropriate content,
                please use this form to report it to us. Simply provide a brief explanation of why you believe it violates our
                community guidelines in the 500-character text box.
                Our team will review your report as soon as possible and take appropriate action, which may include removing the
                content and contacting the user responsible. Rest assured that your identity will remain anonymous throughout
                the process.
                Thank you for your cooperation in helping us maintain a positive and safe community for all users.
            </p>
        </header>
        <form action="<%= request.getContextPath()%>/reader/reportPost" method="POST" id="report-form">
            <div class="mb-3">
                <label for="reporterId" class="form-label">Reporter ID</label>
                <input type="text" class="form-control" id="reporterId" name="reporterId" placeholder="Enter post URL" readonly value="${param.reporterId}">
            </div>
            <div class="mb-3">
                <label for="postId" class="form-label">Post ID</label>
                <input type="text" class="form-control" id="postId" name="postId" placeholder="Enter post URL" readonly value="${param.postId}">
            </div>
            <div class="mb-3">
                <label for="reason" class="form-label">Reason for reporting</label>
                <textarea required  class="form-control" name="reason" id="reason" rows="3" maxlength="500" placeholder="Please provide a brief explanation"></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
        </form>
    </div>
</body>
</html>
