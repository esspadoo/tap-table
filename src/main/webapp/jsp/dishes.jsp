<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dishes &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" type="module"></script>
    <script src="<c:url value='/js/dishes.js'/>" type="module"></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main class="dishes-page">
      <div class="header">
        <div>
          <h1>Dishes</h1>
          <span class="dishes-status" data-status>Loading dishes...</span>
        </div>
        <div>
          <div class="search-wrap">
            <span class="search-icon">&#x1F50E;</span>
            <input
              type="search"
              id="dishes-search"
              placeholder="Search&hellip;"
              autocomplete="off"
            />
          </div>
        </div>
      </div>
      <section
        class="catalog-grid"
        data-grid
        aria-label="Available dishes"
      ></section>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
