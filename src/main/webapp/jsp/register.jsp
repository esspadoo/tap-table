<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Register - TapTable</title>
</head>
<body>
  <form method="post" action="${pageContext.request.contextPath}/register">
    <h2>TapTable</h2>

    <c:if test="${not empty error}">
      <div class="error"><c:out value="${error}" /></div>
    </c:if>

    <label for="name">Name</label>
    <input type="text" id="name" name="name" required />

    <label for="surname">Surname</label>
    <input type="text" id="surname" name="surname" required />

    <label for="username">Username</label>
    <input type="text" id="username" name="username" required />

    <label for="email">Email</label>
    <input type="email" id="email" name="email" required />

    <label for="phone_number">Phone number</label>
    <input type="tel" id="phone_number" name="phone_number" required />

    <label for="password">Password</label>
    <input type="password" id="password" name="password" required />

    <label for="confirm_password">Confirm password</label>
    <input type="password" id="confirm_password" name="confirm_password" required />

    <button type="submit">Register</button>

    <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a></p>
  </form>
</body>
</html>
