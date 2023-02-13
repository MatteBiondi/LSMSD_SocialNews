<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/paging.css">

<nav aria-label="Page navigation example" id="pagination-menu">
  <ul class="pagination justify-content-around pagination-lg">
    <li class="page-item disabled" id="previous-button">
      <span aria-hidden="true" class="page-link bi-arrow-left" aria-label="Previous" id="previous"></span>
    </li>
    <li class="page-item disabled" id="next-button">
      <span aria-hidden="true" class="page-link bi-arrow-right" aria-label="Next" id="next"></span>
    </li>
  </ul>
</nav>