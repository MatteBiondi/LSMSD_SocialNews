<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap-select-country@4.2.0/dist/js/bootstrap-select-country.min.js"></script>
    <script type="module" src="${pageContext.request.contextPath}/scripts/admin/addReporter.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select-country@4.2.0/dist/css/bootstrap-select-country.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/template.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/addReporter.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
    <title>SocialNews</title>
</head>
<body>
<header id="header">
    <jsp:include page="../../common/admin/topNavbar.jsp" >
        <jsp:param name="page" value="addReporter" />
    </jsp:include>
</header>
<section id="content">
    <div id="message"></div>
    <div class="wrapper">
        <div class="h4">Reporter information</div>
        <form id="reporter-form" class="row g-3 needs-validation" novalidate>
            <div class="col-md-6">
                <label for="email" class="form-label">Email</label>
                <input type="email" name="email" class="form-control" id="email" required>
            </div>
            <div class="col-md-6">
                <label for="password" class="form-label">Password</label>
                <input minlength="3" maxlength="16" type="password" name="password" class="form-control" id="password"
                       required autocomplete="on">
            </div>
            <div class="col-md-6">
                <label for="first-name" class="form-label">First Name</label>
                <input pattern="[a-zA-Z ]*" type="text" name="firstName" class="form-control" id="first-name" required>
            </div>
            <div class="col-md-6">
                <label for="last-name" class="form-label">Last Name</label>
                <input pattern="[a-zA-Z ]*" type="text" name="lastName" class="form-control" id="last-name" required>
            </div>
            <div class="col-md-6">
                <label class="form-label" for="gender">Gender</label>
                <select class="form-select" id="gender" name="gender" required>
                    <option>Male</option>
                    <option>Female</option>
                    <option>Other</option>
                </select>
            </div>
            <div class="col-md-6">
                <label class="form-label" for="dob">Date of birth</label>
                <input type="date" class="form-control" id="dob" name="dateOfBirthday">
            </div>
            <div class="col-md-6">
                <label for="address-street" class="form-label">Address street</label>
                <input pattern="[a-zA-Z ]*" type="text" class="form-control" id="address-street" name="addressStreet">
            </div>
            <div class="col-md-6">
                <label for="address-number" class="form-label">Address number</label>
                <input pattern="[a-zA-Z0-9]*" type="text" class="form-control" id="address-number"
                       name="addressNumber">
            </div>
            <div class="col-md-6">
                <label for="city" class="form-label">City</label>
                <input pattern="[a-zA-Z ]*" type="text" class="form-control" id="city" name="city">
            </div>
            <div class="col-md-4">
                <label for="country" class="form-label">State</label>
                <select id="country" name="country" class="form-select" data-flag="true" data-live-search="true"
                        required></select>
            </div>
            <div class="col-md-2">
                <label for="zip" class="form-label">Zip</label>
                <input pattern="[0-9]*" type="text" name="zip" class="form-control" id="zip">
            </div>
            <div class="col-md-6">
                <label for="cell" class="form-label">Telephone</label>
                <input class="form-control" type="tel" id="cell" name=cell">
            </div>
            <div class="col-md-6">
                <label for="picture" class="form-label">Profile image</label>
                <input class="form-control" type="file" accept="image/*" id="picture" name="picture">
            </div>
            <div class="col-12">
                <button id="submit" class="btn btn-primary">Register reporter</button>
            </div>
        </form>
    </div>

</section>
</body>
</html>
