<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="activePage" value="dishes" />
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dishes &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" type="module"></script>
    <script src="<c:url value='/js/dishes-manage.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="dishes-manage-page" class="dash-page">
      <%@ include file="../../fragments/dash-tabs.jsp" %>

      <div class="dash-content">
        <div class="header">
          <div>
            <h1>Dishes</h1>
            <span id="dishes-count"></span>
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
            <a
              href="<c:url value='/dashboard/dishes/create'/>"
              class="btn btn-sm btn-primary"
            >
              &plus; New dish
            </a>
          </div>
        </div>

        <div id="alert-banner" class="alert alert-destructive" hidden>
          <span id="alert-message"></span>
        </div>

        <div class="data-table-wrap" hidden>
          <table class="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody id="dishes-tbody"></tbody>
          </table>
        </div>
        <div id="empty-state" class="table-empty-state" hidden>
          <p class="table-empty-title">No dishes found</p>
          <p class="table-empty-hint">Try a different search or add a new dish.</p>
        </div>
      </div>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
