<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login - TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
  </head>
  <body>
    <div class="page-center">
      <div class="card">
        <div class="card-header">
          <h1>TapTable</h1>
          <p>Sign in to your account</p>
        </div>

        <form class="form" method="post" action="<c:url value='/login'/>">
          <div class="form-group">
            <label class="required" for="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              required
              autofocus
            />
          </div>
          <div class="form-group">
            <label class="required" for="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              required
            />
          </div>
          <button class="btn btn-primary" type="submit">Sign in</button>
        </form>

        <c:if test="${not empty error}">
          <div class="alert alert-destructive">
            <c:out value="${error}" />
          </div>
        </c:if>

        <p class="card-footer">
          Don't have an account?
          <a class="link" href="<c:url value='/register'/>">Register</a>
        </p>
      </div>
    </div>
  </body>
</html>
