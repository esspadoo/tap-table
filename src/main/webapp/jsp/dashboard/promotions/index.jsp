<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="activePage" value="promotions" />
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Promotions &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/promotions-manage.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="promotions-manage-page" data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
      <%@ include file="../../fragments/dash-tabs.jsp" %>

      <div class="dash-content">
        <div class="header">
          <div>
            <h1>Promotions</h1>
            <span id="promotions-count"></span>
          </div>
          <div>
            <div class="search-wrap">
              <span class="search-icon">&#x1F50E;</span>
              <input
                type="search"
                id="promotions-search"
                placeholder="Search&hellip;"
                autocomplete="off"
              />
            </div>
            <c:if test="${user_role == 'ADMIN'}">
              <a
                href="<c:url value='/dashboard/promotions/create'/>"
                class="btn btn-sm btn-primary"
              >
                &plus; New promotion
              </a>
            </c:if>
          </div>
        </div>

        <div id="alert-banner" class="alert alert-destructive" hidden>
          <span id="alert-message"></span>
        </div>

        <div class="data-table-wrap" hidden>
          <table class="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Discount</th>
                <th>Description</th>
                <th>Valid from</th>
                <th>Valid to</th>
              </tr>
            </thead>
            <tbody id="promotions-tbody"></tbody>
          </table>
        </div>
        <div id="empty-state" class="table-empty-state" hidden>
          <p class="table-empty-title">No promotions found</p>
          <p class="table-empty-hint">
            Try a different search or add a new promotion.
          </p>
        </div>
      </div>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
