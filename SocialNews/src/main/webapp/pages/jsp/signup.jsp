<%@ page contentType="text/html;charset=UTF-8" %>

<!-- Page containing user signup form -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Social News - Sign Up form</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/signup.css" type="text/css" media="screen">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css"
          rel="stylesheet" integrity="sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css"
          integrity="sha384-xeJqLiuOvjUBq3iGOjvSQSIlwrpqjSHXpduPd6rQpuiM3f5/ijby8pCsnbu5S81n"
          crossorigin="anonymous">

    <script src="//unpkg.com/jquery@3.4.1/dist/jquery.min.js"></script>
    <script src="//unpkg.com/bootstrap-select-country@4.0.0/dist/js/bootstrap-select-country.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<main class="w-50 m-auto">
    <h1 class="mb-3 fw-normal">Social News</h1>

    <!-- Signup form sends data to loginServlet by post method -->
    <form  action="<%= request.getContextPath()%>/signup" method="post" autocomplete="on">
        <h3 class="mb-4 fw-normal text-primary"> Signup Form</h3>
        <div class="container">

            <!-- Email field -->
            <div class="mb-3">
                <label for="EnterEmail" class="form-label">Email: </label>
                <input type="email" class="form-control" id="EnterEmail" name="email" required>
            </div>

            <!-- Name field -->
            <div class="mb-3">
                <label for="EnterName" class="form-label">Name: </label>
                <input type="text" class="form-control" id="EnterName" name="name" required>
            </div>

            <!-- Surname field -->
            <div class="mb-3">
                <label for="EnterSurname" class="form-label">Surname: </label>
                <input type="text" class="form-control" id="EnterSurname" name="surname" required>
            </div>

            <!-- Gender field -->
            <div class="mb-3">
                <label class="form-label" for="gender">Gender: </label>
                <select class="form-select" id="gender" name="gender" required>
                    <option>Male</option>
                    <option>Female</option>
                </select>
            </div>

            <!-- Country field -->
            <div class="mb-3">
                <label class="form-label" for="country">Country: </label>
                <select class="form-select country-field bfh-countries" data-live-search="true" id="country" name="country" required></select>
            </div>

            <!-- Password field -->
            <div class="mb-3">
                <label for="password" class="form-label">Password: </label>
                <input type="password" class="form-control" id="password" placeholder="Enter Password" name="password" required>
            </div>

            <button type="submit" class="btn btn-primary">SignUp</button>
            <button type="reset" class="btn btn-primary">Cancel</button>

            <div class="mb-3">
                <!-- If present, get message coming as request or session attribute and show it -->
                <%
                    String message = (String) request.getAttribute("message");
                    if(message != null) {
                %>
                <div class="form-text error-message"><%=message%></div>
                <%
                    }
                %>

                <!-- Section with link to login page -->
                <div class="form-text">Already a member?
                    <a href="<%= request.getContextPath()%>/login"> click here to login </a>
                </div>
            </div>
        </div>
    </form>
</main>
<script>
    $('.country-field').countrypicker();
</script>
</body>
</html>