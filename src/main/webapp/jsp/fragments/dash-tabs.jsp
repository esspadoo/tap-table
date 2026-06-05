<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="dash-sidebar" aria-label="Dashboard navigation">
  <a href="<c:url value='/dashboard'/>"<c:if test="${activePage == 'overview'}"> class="active"</c:if>>Overview</a>
  <c:if test="${user_role == 'ADMIN'}">
    <a href="<c:url value='/dashboard/users'/>"<c:if test="${activePage == 'users'}"> class="active"</c:if>>Users</a>
  </c:if>
  <a href="<c:url value='/dashboard/profile'/>"<c:if test="${activePage == 'profile'}"> class="active"</c:if>>Profile</a>
  <a href="<c:url value='/dashboard/password'/>"<c:if test="${activePage == 'password'}"> class="active"</c:if>>Password</a>
</nav>
