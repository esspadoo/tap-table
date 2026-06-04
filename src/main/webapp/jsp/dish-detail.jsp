<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dish Details &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" type="module"></script>
    <script src="<c:url value='/js/dish-detail.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main id="dish-detail-page">
      <a class="back-link" href="<c:url value='/dishes'/>"
        >&larr; Back to dishes</a
      >

      <div class="container">
        <div class="image">
          <div
            id="dish-image-placeholder"
            class="image-placeholder"
            aria-label="Missing image"
          ></div>
          <img id="dish-image" alt="" />
        </div>

        <div class="info">
          <p id="dish-category"></p>
          <h1 id="dish-name"></h1>
          <p id="dish-description"></p>
          <p id="dish-price"></p>

          <div id="dish-ingredients-section">
            <h2>Ingredients</h2>
            <ul id="ingredients-list"></ul>
          </div>
        </div>
      </div>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
