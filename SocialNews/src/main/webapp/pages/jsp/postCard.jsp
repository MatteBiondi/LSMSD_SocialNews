<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<div id="${param.postId}" reporter="${param.reporterId}" class="post-container container my-5 search-result"
     data-millis-time="${param.postMillisTimestamp}">
    <header class="post-header">
        <c:choose>
            <c:when  test="${ sessionScope.userType == \"reader\" }">
                <c:if test="${!pageContext.request.requestURI.toString().contains('reporter/homepage')}">
                    <a href="${pageContext.request.contextPath}/reporterPage?id=${param.reporterId}" class="option">
                        <!--View profile-->
                        <i class="bi bi-person"></i>
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/reader/reportPost?reporterId=${param.reporterId}&postId=${param.postId}" class="option report-post">
                    <!--Add report-->
                    <i class="bi bi-flag"></i>
                </a>
            </c:when>
            <c:otherwise>
                <span  data-post="${param.postId}" data-reporter="${param.reporterId}" class="option remove-button">
                    <!--Remove post-->
                    <i class="bi bi-trash3"></i>
                </span>
            </c:otherwise>
        </c:choose>
    </header>
    <hr>
    <p class="post-text">${param.postText}</p>
    <footer>
        <c:if test='${not empty param.postHashtags.replace("[", "").replace("]", "").trim()}'>
            <p class="hashtags">
                <c:forEach items="${param.postHashtags}" var="hashtag">
                    #${hashtag.replace("[", "").replace("]", "").trim()}
                </c:forEach>
            </p>
        </c:if>
        <c:if test='${not empty param.postLinks.replace("[", "").replace("]", "").trim()}'>
            <p class="related-links">
                <c:forEach items="${param.postLinks}" var="link">
                    <a href='${link.replace("[", "").replace("]", "").trim()}'>
                            ${link.replace("[", "").replace("]", "").trim()}</a>
                </c:forEach>
            </p>
        </c:if>
        <p class="timestamp">${param.postFormattedTimestamp}</p>
        <hr>
        <c:if  test="${ sessionScope.userType == \"reader\" }">
        <div class="form-group">
            <label for="message"></label><textarea class="form-control new-comment-textarea" id="message" rows="3" placeholder="Enter comment text here"></textarea>
            <button data-reporter="${param.reporterId}" class="write-comment">Publish</button>
        </div>
        </c:if>
        <div class="show-comm-div" page="null">
            <button data-post="${param.postId}" data-reporter="${param.reporterId}" class="show-comm">Show comments</button>
        </div>
    </footer>
</div>