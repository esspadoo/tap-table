<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>404 &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main>
      <div class="error-page">
        <span class="error-code" aria-hidden="true">404</span>

        <div class="error-body">
          <h1>Page not found</h1>
          <p>The page you are looking for does not exist or has been moved.</p>
        </div>

        <div class="error-actions">
          <a class="btn btn-outline" href="<c:url value='/dishes'/>">View Dishes</a>
          <a class="btn btn-primary" href="<c:url value='/'/>">Go to Home</a>
        </div>
      </div>
    </main>

    <%@ include file="../fragments/footer.jsp" %>
  </body>
</html>
