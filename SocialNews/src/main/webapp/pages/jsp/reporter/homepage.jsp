<%@ page import="java.util.Date" %>
<%@ page import="it.unipi.lsmsd.socialnews.dto.PostDTO" %>
<%@ page import="java.util.List" %>
<%
  //Get session attributes
  String userType = (String) session.getAttribute("userType");
  String userID = (String) session.getAttribute("id");

  // Get reporter attributes
  // if(userType=="reporter") {
    String reporterID = (String) request.getAttribute("reporterID");
    String fullName = (String) request.getAttribute("name");
    Integer followers = (Integer) request.getAttribute("followers");
    String location = (String) request.getAttribute("location");
    Date dateOfBirth = (Date) request.getAttribute("dateOfBirth");
    String cell = (String) request.getAttribute("cell");
    String email = (String) request.getAttribute("email");
    Byte[] picture = (Byte[]) request.getAttribute("picture");
    List<PostDTO> postsList = (List<PostDTO>) request.getAttribute("postsList");
  // }
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" crossorigin="anonymous">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.1/css/all.css" integrity="sha384-vp86vTRFVJgpjF9jiIGPEEqYqlDwgyBgEF109VFjmqGmIY/Y4HV4d3Gp2irVfcrp" crossorigin="anonymous">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reporterHomepage.css" type="text/css" media="screen">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" type="text/css" media="screen">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://code.jquery.com/jquery-3.6.1.js" integrity="sha256-3zlB5s2uwoUzrXK3BT7AX3FyvojsraNFxCc2vC/7pNI=" crossorigin="anonymous"></script>
  <script src="${pageContext.request.contextPath}/scripts/reporter/homepage.js"></script>
</head>
<body>
<!-- Navbar section -->
<jsp:include page="../../common/navbar.jsp">
  <jsp:param name="userType" value="reporter" />
</jsp:include>
<header>
  <div id="reporter">
    <div id="main-info" class="row header">
      <div class="col-sm-3">
        <img src="https://via.placeholder.com/150x150.png" class="rounded-circle" alt="Profile Picture">
        <div class="col-sm-9">
          <h4 id="reporter-name"><%=fullName%></h4>
          <h2 id="role">Reporter</h2>
          <%
            if(userType=="reader") {
              // todo: gestire il caso di follow-unfollow
          %>
          <button id="follow-button" type="button" class="btn btn-outline-primary">Follow</button>
          <%
            }
          %>
        </div>
      </div>
      <div id="followers-col" class="col-sm-4 reporter-followers">
        <h2>Followers</h2>
        <p id="followers-number"><%=followers%></p>
      </div>
    </div>
    <div class="info-div row header">
      <div class="info-and-contacs col-sm-12">
        <h2>Info & Contacts</h2>
        <p><%=dateOfBirth%></p>
        <p><%=location%></p>
        <div id="contact-info">
          <p><%=cell%></p>
          <p><%=email%></p>
        </div>
      </div>
    </div>
  </div>

</header>
<div class="post-container container my-5">
  <h2 id="add-post-title">Write a new post</h2>
  <div class="form-group">
    <textarea class="form-control" id="message" rows="3" placeholder="Enter post text here"></textarea>
    <input id="hashtags-input" type="text" class="form-control" id="contentTags" placeholder="Enter content hashtags (separated by space)">
    <input id="related-links-input" type="text" class="form-control" placeholder="Enter related links (separated by space)">
  </div>
  <button id="write-post" onclick="publishNewPost('<%=userID%>')">Publish</button>
</div>
<%
  if(postsList.size()!=0) {
    for(PostDTO post:postsList) {
%>
<div class="post-container container my-5">
  <header>
    <%
      if(userType.equals("reader")) {
    %>
    <p id="report" class="option">report</p>
    <%
      }
      else if(userID.equals(reporterID) || userType.equals("admin")) {
    %>
    <p id="remove" class="option">remove</p>
    <%
      }
    %>
  </header>
  <body>
  <p class="post-text"><%=post.getText()%></p>
  <h4 class="show-comm text-center mb-4">Show comments</h4>
  </body>
</div>

<%
    }
  }
  else {
%>
  <p id="no-post-msg">No post added.</p>
<%
    }
%>
<!-- todo: paging for posts -->
</body>
</html>
