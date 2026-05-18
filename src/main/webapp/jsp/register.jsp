<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Register - TapTable</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
  </head>
  <body>
    <div class="page-center">
      <div class="card" style="max-width: 30rem;">

        <div class="card-header">
          <h1 class="card-title">TapTable</h1>
          <p class="card-description">Create your account</p>
        </div>

        <form class="form" method="post" action="${pageContext.request.contextPath}/register">
          <div class="form-row">
            <div class="form-group">
              <label class="label required" for="name">Name</label>
              <input class="input" type="text" id="name" name="name" required autofocus />
            </div>
            <div class="form-group">
              <label class="label required" for="surname">Surname</label>
              <input class="input" type="text" id="surname" name="surname" required />
            </div>
          </div>

          <div class="form-group">
            <label class="label required" for="phone_number">Phone number</label>
            <input class="input" type="tel" id="phone_number" name="phone_number" placeholder="+39 333 123 4567" required />
          </div>

          <div class="form-group">
            <label class="label required" for="username">Username</label>
            <input class="input" type="text" id="username" name="username" required />
          </div>

          <div class="form-group">
            <label class="label required" for="email">Email</label>
            <input class="input" type="email" id="email" name="email" required />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="label required" for="password">Password</label>
              <input class="input" type="password" id="password" name="password" required />
            </div>
            <div class="form-group">
              <label class="label required" for="confirm_password">Confirm</label>
              <input class="input" type="password" id="confirm_password" name="confirm_password" required />
            </div>
          </div>

          <button class="btn btn-primary" type="submit">Create account</button>
        </form>

        <c:if test="${not empty error}">
          <div class="alert alert-destructive">
            <c:out value="${error}" />
          </div>
        </c:if>

        <p class="card-footer">
          Already have an account?
          <a class="link" href="${pageContext.request.contextPath}/login">Sign in</a>
        </p>

      </div>
    </div>
  </body>
</html>
