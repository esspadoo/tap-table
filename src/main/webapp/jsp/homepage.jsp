<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main id="home-page" class="hero">
      <h1>TapTable</h1>
      <p>
        Fresh ingredients, great dishes. Create an account and order right from
        your table.
      </p>
      <div class="hero-actions">
        <a class="btn btn-primary" href="<c:url value='/dishes'/>">
          Browse dishes
        </a>
        <a class="btn btn-outline" href="<c:url value='/ingredients'/>">
          Browse ingredients
        </a>
      </div>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
