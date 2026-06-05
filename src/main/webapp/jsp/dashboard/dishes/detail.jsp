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
    <script src="<c:url value='/js/dish-dashboard-detail.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="dish-detail-page">
      <div class="dash-page-header">
        <nav id="breadcrumb" aria-label="Breadcrumb">
          <ol>
            <li><a href="<c:url value='/dashboard'/>">Dashboard</a></li>
            <li><a href="<c:url value='/dashboard/dishes'/>">Dishes</a></li>
            <li><span aria-current="page">Detail</span></li>
          </ol>
        </nav>
        <h1 id="page-title">Dish Detail</h1>
      </div>

      <div id="alert-banner" class="alert alert-destructive is-hidden">
        <span id="alert-message"></span>
      </div>

      <div class="data-detail-wrap">
        <div class="detail-image-wrap">
          <div id="dish-image-placeholder" class="image-placeholder" aria-label="No image"></div>
          <img id="dish-image" alt="Dish image" class="is-hidden" />
        </div>

        <div class="detail-info">
          <p id="dish-category" class="detail-category"></p>
          <h2 id="dish-name"></h2>
          <p id="dish-description" class="detail-description"></p>
          <p id="dish-price" class="detail-price"></p>

          <div id="dish-ingredients-section" class="is-hidden">
            <h3>Ingredients</h3>
            <ul id="dish-ingredients-list"></ul>
          </div>
        </div>
      </div>

      <div class="detail-actions">
        <a href="<c:url value='/dashboard/dishes'/>" class="btn btn-outline">Back</a>
        <a id="edit-link" href="#" class="btn btn-primary">Edit</a>
      </div>
    </main>
  </body>
</html>
