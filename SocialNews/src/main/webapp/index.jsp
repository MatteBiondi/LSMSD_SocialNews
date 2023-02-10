<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Welcome to Our Social Network</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css" type="text/css" media="screen">
</head>
<body class="background">
<div class="container my-5">
    <h1 class="text-center">Welcome to Social News</h1>
    <div class="d-flex align-items-center">
        <img src="https://cdn-icons-png.flaticon.com/512/2965/2965879.png" alt="Newspaper icon" style="margin-right: 15px;">
        <p class="text-center description">Social News is a platform for connecting and sharing news and information. Here, you can find the latest news posted by our certified reporters, join discussions and share your thoughts with others.</p>
    </div>
    <div class="row">
        <div class="col-sm-4 offset-sm-2">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Readers</h5>
                    <p class="card-text">Readers can sign up and read all the news posted by certified reporters and leave comments on the news they read.</p>
                </div>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Reporters</h5>
                    <p class="card-text">Reporters are certified journalists who post news and updates on our platform. Only their news posts are visible to readers.</p>
                </div>
            </div>
        </div>
    </div>
    <h2 id="join-us" class="text-center my-5">Join Us!</h2>
    <p id="join-div" class="text-center description">Sign up today as a reader or login to access all the latest news and join the conversation. </p>
    <p id="buttons-div" class="text-center">
        <a href="<%= request.getContextPath()%>/signup" class="btn btn-primary">Sign Up</a>
        <a href="<%= request.getContextPath()%>/login" class="btn btn-secondary">Login</a>
    </p>
</div>
</body>
</html>
