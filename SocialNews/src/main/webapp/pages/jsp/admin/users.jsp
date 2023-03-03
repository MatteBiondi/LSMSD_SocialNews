<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="pageSize" scope="request" type="java.lang.Integer"/>
<c:set
        var="baseURL"
        value="${fn:replace(
            pageContext.request.requestURL,
            fn:substring(pageContext.request.requestURI, 0, fn:length(pageContext.request.requestURI)),
            pageContext.request.contextPath
        )}"
/>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/admin/users.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/users.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <title>SocialNews</title>
</head>

<body>

<header id="header">
    <jsp:include page="../../common/admin/topNavbar.jsp" >
        <jsp:param name="page" value="users" />
    </jsp:include>
</header>

<section id="content">
    <div id="report-modal" class="modal fade modal-xl" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5">Reports</h1>
                </div>
                <div class="modal-body">
                    <ul id="report-content-list" class="list-group report-list"><!-- FILLED BY AJAX --></ul>
                </div>
                <div class="d-flex modal-footer">
                    <div class="d-flex justify-content-between w-100">
                        <div>Page <span id="page">1</span></div>
                        <div>
                            <button id="report-prev" type="button" class="btn btn-primary">Prev</button>
                            <button id="report-next" type="button" class="btn btn-primary">Next</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
        </div>
        </div>
    </div>
    <div id="post-modal" class="modal fade modal-xl" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5">Post</h1>
                </div>
                <div class="modal-body">
                    <div id="post"><!-- FILLED BY AJAX --></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <zing-grid id="users-table" height="0px" caption="Registered ${param.type}" search
    page-size="${pageSize}"
               align="center" zebra column-resizable frozen-columns-left="1">
        <zg-data>
            <zg-param name="src"
                      value="${baseURL}/admin/users?type=${param.type}&data=users">
            </zg-param>
            <zg-param name="recordPath" value="users"></zg-param>
            <!-- PAGING -->
            <zg-param name="loadByPage" value="true"></zg-param>
            <zg-param name="prevIDPath" value="prev"></zg-param>
            <zg-param name="nextIDPath" value="next"></zg-param>
            <zg-param name="prevIDKey" value="prevOffset"></zg-param>
            <zg-param name="nextIDKey" value="nextOffset"></zg-param>
            <!-- SEARCH -->
            <zg-param name="searchKey" value="search"></zg-param>
            <!-- DELETE -->
            <zg-param name="deleteOptions"
                      value='{"method":"DELETE", "src":"${baseURL}/admin/users", "queryString":"?type=${param.type}"}'>
            </zg-param>
        </zg-data>
        <c:choose>
            <c:when test='${param.type.equals("readers")}'>
                <zg-colgroup>
                    <zg-column index="email"></zg-column>
                    <zg-column index="fullName"></zg-column>
                    <zg-column index="gender"></zg-column>
                    <zg-column index="country"></zg-column>
                    <zg-column header="Delete" type="custom" index="id" cell-class="render" >
                        <zg-button action="removerecord" data-col="remove">
                            <zg-label slot="label"></zg-label>
                            <i slot="icon"></i>
                        </zg-button>
                    </zg-column>
                </zg-colgroup>
            </c:when>
            <c:otherwise>
                <zg-colgroup>
                    <zg-column index="email"></zg-column>
                    <zg-column index="fullName"></zg-column>
                    <zg-column index="gender"></zg-column>
                    <zg-column index="location"></zg-column>
                    <zg-column index="dateOfBirth"></zg-column>
                    <zg-column index="cell"></zg-column>
                    <zg-column header="Delete" type="custom" index="id" cell-class="render" >
                        <zg-button action="removerecord" data-col="remove">
                            <zg-label slot="label"></zg-label>
                            <i slot="icon"></i>
                        </zg-button>
                    </zg-column>
                    <zg-column header="Homepage" type="custom" index="id" cell-class="render">
                        <zg-button data-id="[[index.id]]" data-col="home">
                            <zg-label slot="label"></zg-label>
                            <i slot="icon"></i>
                        </zg-button>
                    </zg-column>
                    <zg-column header="Reports" index="id,numOfReport" cell-class="renderReport">
                        <zg-button data-num-of-report="[[index.numOfReport]]"
                                   data-id="[[index.id]]" data-col="report">
                            <i slot="icon"></i>
                        </zg-button>
                    </zg-column>
                </zg-colgroup>
            </c:otherwise>
        </c:choose>
        <zg-footer>
            <zg-pager>
                <zg-button slot="left" action="prevpage"></zg-button>
                <zg-text slot="center" value="currpage"></zg-text>
                <zg-button slot="right" action="nextpage"></zg-button>
            </zg-pager>
        </zg-footer>
        <zg-load-mask>
            <div class="spinner d-flex justify-content-center">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        </zg-load-mask>
    </zing-grid>
</section>
</body>
</html>
