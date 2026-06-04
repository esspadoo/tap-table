<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dishes - TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/dishes.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main class="dishes-page">
      <header class="dishes-header">
        <h1>Dishes</h1>
        <p>Browse the available dishes</p>
      </header>

      <p class="dishes-status" data-status>Loading dishes...</p>
      <section
        class="dishes-grid"
        data-grid
        aria-label="Available dishes"
      ></section>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
