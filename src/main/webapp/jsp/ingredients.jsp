<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Ingredients &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/ingredients.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main class="ingredients-page">
      <div class="header">
        <div>
          <h1>Ingredients</h1>
          <span id="ingredients-status" class="ingredients-status"
            >Loading ingredients...</span
          >
        </div>
        <div>
          <div class="search-wrap">
            <span class="search-icon">&#x1F50E;</span>
            <input
              type="search"
              id="ingredients-search"
              placeholder="Search&hellip;"
              autocomplete="off"
            />
          </div>
        </div>
      </div>
      <section
        id="ingredients-grid"
        class="catalog-grid"
        aria-label="Available ingredients"
      ></section>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
