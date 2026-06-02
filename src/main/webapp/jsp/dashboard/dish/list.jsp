<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dishes &mdash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/dish.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main class="dashboard">
      <div class="dashboard-header">
        <nav class="breadcrumb" aria-label="breadcrumb">
          <a href="<c:url value='/dashboard'/>">Dashboard</a>
          <span class="breadcrumb-sep">/</span>
          <span>Dishes</span>
        </nav>
        <h1>Dishes</h1>
        <p>Manage menu dishes</p>
      </div>

      <section class="dashboard-section">
        <div class="dashboard-section-header">
          <h2 class="section-title">All Dishes</h2>
          <a href="<c:url value='/dashboard/dish/create'/>" class="btn btn-primary" style="width: auto;">
            New dish
          </a>
        </div>

        <p id="dishes-status" class="dashboard-status">Loading dishes...</p>
        <section
          id="dishes-grid"
          class="dishes-grid"
          aria-label="Available dishes"
        ></section>
      </section>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>