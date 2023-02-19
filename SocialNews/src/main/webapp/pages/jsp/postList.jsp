<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="postsList" scope="request" type="java.util.List"/>


<link rel="stylesheet" href="${pageContext.request.contextPath}/css/postCard.css" type="text/css" media="screen">

<div id="post-items">
    <c:choose>
        <c:when test="${postsList.size() != 0}">
            <c:forEach items="${postsList}" var="post">
                <jsp:include page="postCard.jsp">
                    <jsp:param name="postId" value="${post.getId()}" />
                    <jsp:param name="reporterId" value="${post.getReporterId()}" />
                    <jsp:param name="postText" value="${post.getText()}" />
                    <jsp:param name="postHashtags" value="${post.getHashtags()}" />
                    <jsp:param name="postLinks" value="${post.getLinks()}" />
                    <jsp:param name="postFormattedTimestamp" value="${post.getTimestamp()}" />
                    <jsp:param name="postMillisTimestamp" value="${post.getTimestamp().getTime()}" />
                </jsp:include>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p id="no-post-msg">No post to visualize</p>
        </c:otherwise>
    </c:choose>
</div>