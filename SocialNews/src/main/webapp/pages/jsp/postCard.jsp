<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<div id="${param.postId}" class="post-container container my-5 search-result"
     data-millis-time="${param.postMillisTimestamp}">
    <header class="post-header">
        <c:choose>
            <c:when  test="${ sessionScope.userType == \"reader\" }">
                <a href="${pageContext.request.contextPath}/reader/reporterPage?id=${param.reporterId}" class="option">
                    <!--View profile-->
                    <i class="bi bi-person"></i>
                </a>
                <span  data-ref="${param.postId}" class="option">
                    <!--Add report-->
                    <i class="bi bi-flag"></i>
                </span>
            </c:when>
            <c:otherwise>
                <span  data-ref="${param.postId}" class="option">
                    <!--Remove post-->
                    <i class="bi bi-trash3"></i>
                </span>
            </c:otherwise>
        </c:choose>
    </header>
    <hr>
    <p class="post-text">${param.postText}</p>
    <footer>
        <p class="hashtags">${param.postHashtags}</p>
        <p class="related-links">${param.postLinks}</p>
        <p class="timestamp">${param.postFormattedTimestamp}</p>
        <hr>
        <div class="show-comm-div">
            <button class="show-comm">Show comments</button>
        </div>
    </footer>
</div>

<!--TODO: js post (es report and show comment)-->