<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Login - TapTable</title>
</head>
<body>
  <form method="post" action="${pageContext.request.contextPath}/login">
    <h2>TapTable</h2>

    <c:if test="${not empty error}">
      <div class="error"><c:out value="${error}" /></div>
    </c:if>

    <label for="email">Email</label>
    <input type="email" id="email" name="email" required autofocus />

    <label for="password">Password</label>
    <input type="password" id="password" name="password" required />

    <button type="submit">Login</button>

    <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register</a></p>
  </form>
</body>
</html>
