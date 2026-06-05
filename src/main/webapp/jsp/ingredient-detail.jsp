<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Ingredient Details &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" type="module"></script>
    <script src="<c:url value='/js/ingredient-detail.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main id="ingredient-detail-page">
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
          <p id="ingredient-frozen" hidden></p>
          <h1 id="ingredient-name"></h1>

          <div id="allergens-section" hidden>
            <h2>Allergens</h2>
            <ul id="allergens-list"></ul>
          </div>
        </div>
      </div>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
