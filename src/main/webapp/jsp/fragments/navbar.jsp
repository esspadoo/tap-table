<nav
  class="navbar close"
  data-ctx="<c:out value='${pageContext.request.contextPath}'/>"
>
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
  <div class="menu">
    <div class="items">
      <a href="<c:url value='/ingredients'/>">Ingredients</a>
    </div>
    <div class="actions">
      <a class="btn btn-outline btn-md" href="<c:url value='/login'/>">Sign in</a>
      <a class="btn btn-primary btn-md" href="<c:url value='/register'/>">Register</a>
    </div>
  </div>
</nav>
