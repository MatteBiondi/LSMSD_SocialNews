<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <title>SocialNews</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
</head>
<body>
<header>
    <jsp:include page="../../common/admin/topNavbar.jsp" >
        <jsp:param name="page" value="dashboard" />
    </jsp:include>
</header>
</body>
</html>
