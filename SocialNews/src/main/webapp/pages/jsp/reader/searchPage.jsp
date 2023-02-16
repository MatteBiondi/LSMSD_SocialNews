<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Social News - Search</title>
    <meta charset="UTF-8">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/contentPage.css" type="text/css" media="screen">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css"
          rel="stylesheet" integrity="sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css"
          integrity="sha384-xeJqLiuOvjUBq3iGOjvSQSIlwrpqjSHXpduPd6rQpuiM3f5/ijby8pCsnbu5S81n"
          crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.6.1.js"
            integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI="
            crossorigin="anonymous">
    </script>
    <script src="${pageContext.request.contextPath}/scripts/reader/searchPage.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <!-- Navbar section -->
    <jsp:include page="../../common/navbar.jsp"/>

    <h1 class="content-title">Search results:</h1>

    <!-- Pagination section -->
    <jsp:include page="../../common/pagination.jsp"/>

    <div id="result_list">
        <!-- Filled by AJAX -->
    </div>
</body>
</html>