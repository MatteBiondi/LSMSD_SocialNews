<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reporterStatistics.css">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/reporter/statistics.js"></script>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <title>SocialNews</title>
</head>
<body>
<header id="header">
    <jsp:include page="/pages/common/navbar.jsp" >
        <jsp:param name="page" value="statistics" />
    </jsp:include>
</header>
<section id="content">
    <div class="d-flex flex-column">
        <div class="widget-row d-flex justify-content-around">
            <div id="hot-posts-statistic" class="widget">
                <h1 class="h4">Hot posts</h1>
                <div class="spinner d-flex justify-content-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id='hottest-posts-grid'></div>
                <div class="controls">
                    <div class="controls">
                        <div class="dropdown settings">
                            <i class="bi bi-gear gear" data-bs-toggle="dropdown" aria-expanded="false"></i>
                            <div class="dropdown-menu">
                                <div class="input-group params">
                                    <label for="hottest-post-count" class="input-group-text">From last: </label>
                                    <input class="form-control param" name="hottest-post-count" type="number"
                                           id="hottest-post-count" step="1" min="1" value="10" data-param="lastN">
                                    <select id="hottest-post-unit" name="hottest-post-unit" aria-label="Period"
                                            class="form-control form-select param" data-param="unitOfTime">
                                        <option value="Hour">Hour</option>
                                        <option value="Day">Day</option>
                                        <option value="Week">Week</option>
                                        <option value="Month" selected>Month</option>
                                        <option value="Year" selected>Year</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <i class="reload bi bi-arrow-clockwise" data-statistic="hottestPosts"></i>
                    </div>
                </div>
            </div>
            <div id="hottest-moment" class="widget">
                <h1 class="h4">Most active moments of the day</h1>
                <div class="spinner d-flex justify-content-center">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div id="hottest-moment-bar"></div>
                <div class="controls">
                    <div class="dropdown settings">
                        <i class="bi bi-gear gear" data-bs-toggle="dropdown" aria-expanded="false"></i>
                        <div class="dropdown-menu">
                            <div class="input-group params">
                                <label for="hottest-moment-count" class="input-group-text">From last: </label>
                                <input class="form-control param" name="hottest-moment-count" type="number"
                                       id="hottest-moment-count" step="1" min="1" value="10" data-param="lastN">
                                <select id="hottest-moment-unit" name="hottest-moment-unit" aria-label="Period"
                                        class="form-control form-select param" data-param="unitOfTime">
                                    <option value="Hour">Hour</option>
                                    <option value="Day">Day</option>
                                    <option value="Week">Week</option>
                                    <option value="Month">Month</option>
                                    <option value="Year" selected>Year</option>
                                </select>
                            </div>
                            <div class="input-group params">
                                <label for="hottest-moment-window" class="input-group-text">Window size: </label>
                                <select id="hottest-moment-window" aria-label="Period" data-param="windowSize"
                                        class="form-control form-select param">
                                    <option value="3" selected>3 hours</option>
                                    <option value="4">4 hours</option>
                                    <option value="6">6 hours</option>
                                    <option value="12">12 hours</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <i class="reload bi bi-arrow-clockwise" data-statistic="hottestMomentsOfDay"></i>
                </div>
            </div>
        </div>
    </div>
</section>

</body>
</html>
