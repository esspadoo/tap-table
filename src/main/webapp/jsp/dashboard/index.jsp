<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Dashboard - TapTable</title>
</head>
<body>
  <h1>Welcome, <c:out value="${user.name}" />!</h1>
  <p><strong>ID:</strong> <c:out value="${user.id}" /></p>
  <p><strong>Username:</strong> <c:out value="${user.username}" /></p>
  <p><strong>Name:</strong> <c:out value="${user.name}" /> <c:out value="${user.surname}" /></p>
  <p><strong>Email:</strong> <c:out value="${user.email}" /></p>
  <p><strong>Phone:</strong> <c:out value="${user.phoneNumber}" /></p>
  <p><strong>Role:</strong> <c:out value="${user.role}" /></p>

  <br />
  <form method="post" action="${pageContext.request.contextPath}/logout">
    <button type="submit">Logout</button>
  </form>
</body>
</html>
