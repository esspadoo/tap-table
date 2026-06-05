<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main id="home-page">

      <section class="home-hero">
        <div class="home-hero-inner">
          <h1 class="home-title">TapTable</h1>
          <p class="home-tagline">
            Browse the menu, place your order from the table, and let the
            kitchen take care of the rest.
          </p>
          <div class="home-actions">
            <a class="btn btn-primary" href="<c:url value='/dishes'/>">
              Browse dishes
            </a>
            <a class="btn btn-outline" href="<c:url value='/ingredients'/>">
              View ingredients
            </a>
          </div>
        </div>
      </section>

      <section class="home-features">
        <div class="home-features-inner">
          <div class="home-feature">
            <h2 class="home-feature-title">Browse the menu</h2>
            <p class="home-feature-body">
              Explore dishes and ingredients, with allergen info and
              availability updated in real time.
            </p>
          </div>
          <div class="home-feature">
            <h2 class="home-feature-title">Order from your table</h2>
            <p class="home-feature-body">
              Add dishes to your cart and confirm your order. No app to
              download, no queue to join.
            </p>
          </div>
          <div class="home-feature">
            <h2 class="home-feature-title">Track and review</h2>
            <p class="home-feature-body">
              Monitor your order status from your account and leave feedback
              once your food is served.
            </p>
          </div>
        </div>
      </section>

      <section class="home-team">
        <div class="home-team-inner">
          <h2 class="home-team-heading">The Team</h2>
          <p class="home-team-context">
            University of Padua, Web Applications A.Y. 2025/2026
          </p>
          <ul class="home-team-list" role="list">
            <li class="home-team-member">
              <span class="home-team-name">Baldan Fabio</span>
              <span class="home-team-id">2203580</span>
            </li>
            <li class="home-team-member">
              <span class="home-team-name">Garberino Alvise</span>
              <span class="home-team-id">2196387</span>
            </li>
            <li class="home-team-member">
              <span class="home-team-name">Merja Klaudio</span>
              <span class="home-team-id">2197815</span>
            </li>
            <li class="home-team-member">
              <span class="home-team-name">Padoan Giancarlo</span>
              <span class="home-team-id">2188345</span>
            </li>
            <li class="home-team-member">
              <span class="home-team-name">Sanavia Thomas</span>
              <span class="home-team-id">2197484</span>
            </li>
          </ul>
        </div>
      </section>

    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
