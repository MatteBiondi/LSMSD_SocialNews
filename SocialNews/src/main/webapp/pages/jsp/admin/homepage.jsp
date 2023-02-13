<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/homepage.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <title>SocialNews - Admin</title>
</head>

<body>

<header id="header">
    <jsp:include page="../../common/admin/topNavbar.jsp" >
        <jsp:param name="page" value="homepage" />
    </jsp:include>
</header>

<section id="content">
    <div class="wrapper">
        <div class="row">
            <div class="col-sm-6">
                <div class="card">
                    <div class="card-header">
                        <i class="fs-3 bi bi-people-fill"></i>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">Registered readers</h5>
                        <p class="card-text">List all readers registered on the platform</p>
                        <a href="${pageContext.request.contextPath}/admin/users?type=readers" id="readers-btn"
                           class="btn btn-primary">Show</a>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="card">
                    <div class="card-header">
                        <i class="fs-3 bi bi-people-fill"></i>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">Registered reporters</h5>
                        <p class="card-text">List all reporters registered on the platform</p>
                        <a href="${pageContext.request.contextPath}/admin/users?type=reporters" id="reporters-btn"
                           class="btn btn-primary">
                            Show</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-6">
                <div class="card">
                    <div class="card-header">
                        <i class="fs-3 bi bi-person-plus-fill"></i>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">New reporter</h5>
                        <p class="card-text">Register a new reporter on the platform</p>
                        <a href="${pageContext.request.contextPath}/admin/addReporter" class="btn btn-primary">Show</a>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="card">
                    <div class="card-header">
                        <i class="fs-3 bi bi-pie-chart-fill"></i>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">Statistics dashboard</h5>
                        <p class="card-text">Visualize some statistics about users and their activities</p>
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-primary">Show</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

</body>
</html>
