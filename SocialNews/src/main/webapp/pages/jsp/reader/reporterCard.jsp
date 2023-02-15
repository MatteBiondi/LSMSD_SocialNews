<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<div class="col-sm-6">
    <div class="card card-container search-result" style="width: 18rem;" id="${param.id}" data-full-name="${param.fullName}">
        <img src="${param.image}" class="card-img-top" alt="Profile image">
        <div class="card-body">
            <h5 class="card-title">${param.fullName}</h5>
            <a href="${pageContext.request.contextPath}/reader/reporterPage?id=${param.id}" class="btn btn-primary">View profile page</a>
        </div>
    </div>
</div>