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
    <nav class="navbar close">
      <!-- Navbar row for mobile -->
      <div class="row">
        <a class="logo" href="<c:url value='/'/>">TapTable</a>
        <button
          class="burger-button"
          aria-label="Toggle menu"
          aria-expanded="false"
        >
          <span></span><span></span><span></span>
        </button>
      </div>

      <!-- Menu -->
      <div class="menu">
        <!-- Menu items -->
        <div class="items">
          <a href="<c:url value='/ingredients'/>">Ingredients</a>
          <a href="<c:url value='/ingredients'/>">Ingredients</a>
          <a href="<c:url value='/ingredients'/>">Ingredients</a>
          <a href="<c:url value='/ingredients'/>">Ingredients</a>
          <a href="<c:url value='/ingredients'/>">Ingredients</a>
          <a href="<c:url value='/ingredients'/>">Ingredients</a>
        </div>

        <!-- Menu actions -->
        <div class="actions">
          <a class="btn btn-outline" href="<c:url value='/login'/>">Sign in</a>
          <a class="btn btn-primary" href="<c:url value='/register'/>">
            Register
          </a>
        </div>
      </div>
    </nav>

    <main class="hero">
      <h1>TapTable</h1>
      <p>
        Fresh ingredients, great dishes. Create an account and order right from
        your table.
      </p>
      <div class="hero-actions">
        <a class="btn btn-primary" href="<c:url value='/register'/>"
          >Create account</a
        >
        <a class="btn btn-outline" href="<c:url value='/ingredients'/>"
          >Browse ingredients</a
        >
      </div>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &mdash; University of Padua</p>
      <p class="footer-authors">
        Baldan Fabio (2203580) &middot; Garberino Alvise (2196387) &middot;
        Merja Klaudio (2197815) &middot; Padoan Giancarlo (2188345) &middot;
        Sanavia Thomas (2197484)
      </p>
    </footer>
  </body>
</html>
