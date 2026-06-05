<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="dash-sidebar" aria-label="Dashboard navigation">
  <a href="<c:url value='/dashboard'/>"<c:if test="${activePage == 'overview'}"> class="active"</c:if>>Overview</a>
  <c:if test="${user_role == 'ADMIN'}">
    <a href="<c:url value='/dashboard/users'/>"<c:if test="${activePage == 'users'}"> class="active"</c:if>>Users</a>
  </c:if>
  <c:if test="${user_role == 'ADMIN' or user_role == 'STAFF'}">
    <a href="<c:url value='/dashboard/dishes'/>"<c:if test="${activePage == 'dishes'}"> class="active"</c:if>>Dishes</a>
  </c:if>
  <c:if test="${user_role == 'ADMIN' or user_role == 'STAFF'}">
    <a href="<c:url value='/dashboard/ingredients'/>"<c:if test="${activePage == 'ingredients'}"> class="active"</c:if>>Ingredients</a>
  </c:if>
  <a href="<c:url value='/dashboard/profile'/>"<c:if test="${activePage == 'profile'}"> class="active"</c:if>>Account</a>
</nav>
