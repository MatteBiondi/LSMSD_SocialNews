<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.zingchart.com/zingchart.min.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/admin/dashboard.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/dashboard.css">

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <title>SocialNews</title>
</head>
<body>
<header id="header">
    <jsp:include page="../../common/admin/topNavbar.jsp" >
        <jsp:param name="page" value="dashboard" />
    </jsp:include>
</header>
<section id="content">
<div class="d-flex flex-column">
    <div class="d-flex justify-content-around">
        <div id="gender-statistic" class="widget">
            <h1 class="h4">Gender statistic</h1>
            <div>
                <div id='gender-statistic-pie'></div>
                <div class="control">
                    <!--button class="btn btn-primary">Reload</button-->
                </div>
            </div>
        </div>
        <div id="most-active-readers"  class="widget">
            <h1 class="h4">Most active readers</h1>
            <div id="most-active-readers-grid"></div>
        </div>
    </div>

    <div class="d-flex justify-content-around">
        <div id="nationality-statistic" class=" widget">
            <h1 class="h4">Nationality statistic</h1>
            <div id='nationality-statistic-pie'></div>
        </div>
        <div id="hottest-moment"  class=" widget">
            <h1 class="h4">Most active moments of the day</h1>
            <div id="hottest-moment-bar"></div>
        </div>
    </div>

</div>

    <!--div id="carousel-statistics" class="carousel slide carousel-dark" data-bs-ride="false">
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#carousel-statistics" data-bs-slide-to="0" class="active"
                    aria-current="true" aria-label="Slide 1"></button>
            <button type="button" data-bs-target="#carousel-statistics" data-bs-slide-to="1" aria-label="Slide 2"></button>
            <button type="button" data-bs-target="#carousel-statistics" data-bs-slide-to="2" aria-label="Slide 3"></button>
            <button type="button" data-bs-target="#carousel-statistics" data-bs-slide-to="3"
                    aria-label="Slide 4"></button>
        </div>
        <div class="carousel-inner">
            <div class="carousel-item active">
                <div class="d-flex justify-content-center">
                    <div id="gender-statistic" class="widget">
                        <h1 class="h4">Gender statistic</h1>
                        <div id='gender-statistic-pie'></div>
                    </div>
                </div>
            </div>
            <div class="carousel-item">
                <div class="d-flex justify-content-center">
                    <div id="nationality-statistic" class="widget">
                        <h1 class="h4">Nationality statistic</h1>
                        <div id='nationality-statistic-pie'></div>
                    </div>
                </div>
            </div>
            <div class="carousel-item">
                <div class="d-flex justify-content-center">
                    <div id="most-active-readers" class="widget">
                        <h1 class="h4">Most active readers</h1>
                        <div id="most-active-readers-grid"></div>
                    </div>
                </div>
            </div>
            <div class="carousel-item">
                <div class="d-flex justify-content-center">
                    <div id="hottest-moment" class="widget">
                        <h1 class="h4">Most active moments of the day</h1>
                        <div id="hottest-moment-bar"></div>
                    </div>
                </div>
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carousel-statistics" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carousel-statistics" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
        </button>
    </div-->
</section>
</body>
</html>
