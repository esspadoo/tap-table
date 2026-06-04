<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Ingredients &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" type="module"></script>
    <script src="<c:url value='/js/ingredients-manage.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="ingredients-manage-page">
      <div class="header">
        <div>
          <h1>Ingredients</h1>
          <span id="ingredients-count"></span>
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
          <a
            href="<c:url value='/dashboard/ingredients/create'/>"
            class="btn btn-sm btn-primary"
          >
            &plus; New ingredient
          </a>
        </div>
      </div>

      <div id="alert-banner" class="alert alert-destructive is-hidden">
        <span id="alert-message"></span>
      </div>

      <div class="data-table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Allergens</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody id="ingredients-tbody"></tbody>
          <tfoot id="empty-state" class="is-hidden">
            <tr>
              <td colspan="3" class="dt-empty">
                <p class="dt-empty-title">No ingredients found</p>
                <p class="dt-empty-hint">
                  Try a different search or add a new ingredient.
                </p>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </main>
  </body>
</html>
