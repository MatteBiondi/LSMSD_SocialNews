<%@ page isErrorPage = "true" contentType="text/html;charset=UTF-8" %>

<!-- Page that rappresents the details for the error occured -->
<html>
<%
    int statusCode = response.getStatus();
%>
<head>
    <title><%= statusCode%> Error occurred - Error Report</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/icon.svg">
</head>
<body>
<h1>An HTTP error occurred</h1>
<hr/>
<p>It seems there is a problem in the web application.</p>
<table>
    <tr>
        <td>Type</td>
        <td>Status report</td>
    </tr>
    <tr>
        <td>Code</td>
        <td><%= statusCode%></td>
    </tr>
    <tr>
        <td>Message</td>
        <td>We apologise for the inconvenience. Please contact the administrator for further information</td>
    </tr>
</table>
<hr/>
<a href="<%= request.getContextPath()%>">Back Home</a>
</body>
</html>