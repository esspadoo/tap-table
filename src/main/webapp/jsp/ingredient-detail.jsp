<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Ingredient Details - TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/ingredient-detail.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main class="ingredient-detail-page">
      <a class="back-link" href="<c:url value='/ingredients'/>"
        >&larr; Back to ingredients</a
      >

      <div class="container">
        <div class="image">
          <div
            id="ingredient-image-placeholder"
            class="image-placeholder"
            aria-label="Missing image"
          ></div>
          <img id="ingredient-image" alt="" />
        </div>

        <div class="info">
          <p id="ingredient-frozen" class="is-hidden"></p>
          <h1 id="ingredient-name"></h1>

          <div id="allergens-section" hidden>
            <h2>Allergens</h2>
            <ul id="allergens-list"></ul>
          </div>
        </div>
      </div>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
