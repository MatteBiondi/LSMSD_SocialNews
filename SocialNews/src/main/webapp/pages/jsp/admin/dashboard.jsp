<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/dashboard.css">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/admin/dashboard.js"></script>
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
        <div class="widget-row d-flex justify-content-around">
            <div id="gender-statistic" class="widget">
                <h1 class="h4">Gender statistic</h1>
                <div class="spinner d-flex justify-content-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id='gender-statistic-pie'></div>
                <div class="controls">
                    <i class="reload bi bi-arrow-clockwise" data-statistic="genderStatistic"></i>
                </div>
            </div>
            <div id="most-active-readers" class="widget">
                <h1 class="h4">Most active readers</h1>
                <div class="spinner d-flex justify-content-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id="most-active-readers-grid"></div>
                <div class="controls">
                    <div class="dropdown settings">
                        <i class="bi bi-gear gear" data-bs-toggle="dropdown" aria-expanded="false"></i>
                        <div class="dropdown-menu">
                            <div class="input-group params">
                                <label for="most-active-readers-counter" class="input-group-text">From last: </label>
                                <input id="most-active-readers-counter" name="most-active-readers-counter"
                                       class="form-control param" type="number" step="1" min="1" value="1"
                                       data-param="lastN">
                                <select id="most-active-readers-unit" aria-label="Period"
                                        class="form-control form-select param" data-param="unitOfTime">
                                    <option value="Hour">Hour</option>
                                    <option value="Day">Day</option>
                                    <option value="Week">Week</option>
                                    <option value="Month">Month</option>
                                    <option value="Year">Year</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <i class="reload bi bi-arrow-clockwise" data-statistic="mostActiveReaders"></i>
                </div>
            </div>
        </div>
        <div class="widget-row d-flex justify-content-around">
            <div id="nationality-statistic" class="widget">
                <h1 class="h4">Nationality statistic</h1>
                <div class="spinner d-flex justify-content-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id='nationality-statistic-pie'></div>
                <div class="controls">
                    <i class="reload bi bi-arrow-clockwise" data-statistic="nationalityStatistic"></i>
                </div>
            </div>
            <div id="most-popular-reporters" class="widget">
                <h1 class="h4">Most popular reporters</h1>
                <div class="spinner d-flex justify-content-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id="most-popular-reporters-grid"></div>
                <div class="controls">
                    <i class="reload bi bi-arrow-clockwise" data-statistic="mostPopularReporters"></i>
                </div>
            </div>
        </div>
    </div>
</section>

</body>
</html>
