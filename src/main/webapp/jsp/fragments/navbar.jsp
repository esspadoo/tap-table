<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav id="navbar" class="navbar close">
  <div class="row">
    <a class="logo" href="<c:url value='/'/>">TapTable</a>
    <button
      id="burger-button"
      class="burger-button"
      aria-label="Toggle menu"
      aria-expanded="false"
    >
      <span></span><span></span><span></span>
    </button>
  </div>
  <div class="menu">
    <div class="items">
      <a href="<c:url value='/dishes'/>">Dishes</a>
      <a href="<c:url value='/ingredients'/>">Ingredients</a>
      <c:if test="${nav_authenticated}">
        <a href="<c:url value='/cart'/>"
          >Cart (<span id="cart-count"></span>)</a
        >
      </c:if>
    </div>
    <div class="actions">
      <c:choose>
        <c:when test="${nav_authenticated}">
          <a class="btn btn-primary btn-md" href="<c:url value='/dashboard'/>"
            >Dashboard</a
          >
          <form method="post" action="<c:url value='/logout'/>">
            <button type="submit" class="btn btn-outline btn-md">
              Sign out
            </button>
          </form>
        </c:when>
        <c:otherwise>
          <a class="btn btn-outline btn-md" href="<c:url value='/login'/>"
            >Sign in</a
          >
          <a class="btn btn-primary btn-md" href="<c:url value='/register'/>"
            >Register</a
          >
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</nav>
