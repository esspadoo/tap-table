<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Users &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main id="users-page" class="dash-page">

      <%@ include file="../fragments/dash-tabs.jsp" %>

      <div class="dash-content">

        <div class="dash-page-header">
          <h1>Users</h1>
          <p>Manage user accounts and roles.</p>
        </div>

        <section class="dashboard-section">
          <h2 class="section-title">All accounts</h2>

          <c:choose>
            <c:when test="${empty users}">
              <p class="empty-state">No users found.</p>
            </c:when>
            <c:otherwise>
              <div class="user-list">
                <c:forEach var="u" items="${users}">
                  <div class="user-row">

                    <div class="user-info">
                      <span class="user-name">
                        <c:out value="${u.name} ${u.surname}" />
                      </span>
                      <span class="user-email">
                        <c:out value="${u.email}" />
                      </span>
                    </div>

                    <div class="user-actions">
                      <c:choose>
                        <c:when test="${u.role.name() == 'ADMIN'}">
                          <span class="badge badge-admin">Admin</span>
                        </c:when>
                        <c:when test="${u.role.name() == 'STAFF'}">
                          <span class="badge badge-staff">Staff</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge badge-customer">Customer</span>
                        </c:otherwise>
                      </c:choose>

                      <c:if test="${u.role.name() != 'ADMIN'}">

                        <c:choose>
                          <c:when test="${u.role.name() == 'CUSTOMER'}">
                            <form method="post" action="<c:url value='/dashboard/users'/>">
                              <input type="hidden" name="_action" value="change_role" />
                              <input type="hidden" name="userId" value="<c:out value='${u.id}'/>" />
                              <input type="hidden" name="newRole" value="STAFF" />
                              <button type="submit" class="btn btn-secondary btn-sm">Make staff</button>
                            </form>
                          </c:when>
                          <c:when test="${u.role.name() == 'STAFF'}">
                            <form method="post" action="<c:url value='/dashboard/users'/>">
                              <input type="hidden" name="_action" value="change_role" />
                              <input type="hidden" name="userId" value="<c:out value='${u.id}'/>" />
                              <input type="hidden" name="newRole" value="CUSTOMER" />
                              <button type="submit" class="btn btn-secondary btn-sm">Remove staff</button>
                            </form>
                          </c:when>
                        </c:choose>

                        <form method="post" action="<c:url value='/dashboard/users'/>"
                              onsubmit="return confirm('Delete this user? This cannot be undone.')">
                          <input type="hidden" name="_action" value="delete_user" />
                          <input type="hidden" name="userId" value="<c:out value='${u.id}'/>" />
                          <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                        </form>

                      </c:if>
                    </div>

                  </div>
                </c:forEach>
              </div>
            </c:otherwise>
          </c:choose>

        </section>

      </div>
    </main>

    <%@ include file="../fragments/footer.jsp" %>
  </body>
</html>
