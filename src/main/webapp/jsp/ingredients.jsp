<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Ingredients - TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/ingredients.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main class="ingredients-page">
      <header class="ingredients-header">
        <h1>Ingredients</h1>
        <p>Browse the available ingredients</p>
      </header>

      <p id="ingredients-status" class="ingredients-status">Loading ingredients...</p>
      <section
        id="ingredients-grid"
        class="ingredients-grid"
        aria-label="Available ingredients"
      ></section>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
