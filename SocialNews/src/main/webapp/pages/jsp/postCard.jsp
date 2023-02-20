<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<div id="${param.postId}" class="post-container container my-5 search-result"
     data-millis-time="${param.postMillisTimestamp}">
    <header class="post-header">
        <c:choose>
            <c:when  test="${ sessionScope.userType == \"reader\" }">
                <a href="${pageContext.request.contextPath}/reporterPage?id=${param.reporterId}" class="option">
                    <!--View profile-->
                    <i class="bi bi-person"></i>
                </a>
                <a href="${pageContext.request.contextPath}/reader/reportPost?reporterId=${param.reporterId}&postId=${param.postId}" class="option report-post">
                    <!--Add report-->
                    <i class="bi bi-flag"></i>
                </a>
            </c:when>
            <c:otherwise>
                <a  data-ref="${param.postId}" class="option">
                    <!--Remove post-->
                    <i class="bi bi-trash3" onclick="removePost('${param.reporterId}','${param.postId}')"></i>
                </a>
            </c:otherwise>
        </c:choose>
    </header>
    <hr>
    <p class="post-text">${param.postText}</p>
    <footer>
        <c:if test="${not empty param.postHashtags}">
            <p class="hashtags">${param.postHashtags.replace("[", "").replace("]", "").replace(",","")}</p>
        </c:if>
        <c:if test="${not empty param.postLinks}">
            <p class="related-links">${param.postLinks.replace("[", "").replace("]", "").replace(",","")}</p>
        </c:if>
        <p class="timestamp">${param.postFormattedTimestamp}</p>
        <hr>
        <div class="show-comm-div">
            <button class="show-comm">Show comments</button>
        </div>
    </footer>
</div>

<!--TODO: js post (es show comment)-->