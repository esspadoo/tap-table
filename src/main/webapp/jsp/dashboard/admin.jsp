<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<c:set var="pendingCount" value="0"/>
<c:set var="completedCount" value="0"/>
<c:set var="cancelledCount" value="0"/>
<c:forEach var="o" items="${orders}">
  <c:choose>
    <c:when test="${o.status == 'PENDING'}"><c:set var="pendingCount" value="${pendingCount + 1}"/></c:when>
    <c:when test="${o.status == 'COMPLETED'}"><c:set var="completedCount" value="${completedCount + 1}"/></c:when>
    <c:otherwise><c:set var="cancelledCount" value="${cancelledCount + 1}"/></c:otherwise>
  </c:choose>
</c:forEach>
<c:set var="totalCount" value="${pendingCount + completedCount + cancelledCount}"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Admin Dashboard &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/admin-dashboard.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main id="admin-dashboard" class="dash-page" data-ctx="<c:out value='${pageContext.request.contextPath}'/>">

      <%@ include file="../fragments/dash-tabs.jsp" %>

      <div class="dash-content">

        <div class="dash-page-header">
          <h1>Overview</h1>
          <p>Welcome, <c:out value="${user.name}" /></p>
        </div>

        <div class="admin-stats">
          <div class="stat-card stat-card-total">
            <span class="stat-card-value"><c:out value="${totalCount}" /></span>
            <span class="stat-card-label">Total orders</span>
          </div>
          <div class="stat-card stat-card-pending">
            <span class="stat-card-value"><c:out value="${pendingCount}" /></span>
            <span class="stat-card-label">Pending</span>
          </div>
          <div class="stat-card stat-card-completed">
            <span class="stat-card-value"><c:out value="${completedCount}" /></span>
            <span class="stat-card-label">Completed</span>
          </div>
          <div class="stat-card stat-card-cancelled">
            <span class="stat-card-value"><c:out value="${cancelledCount}" /></span>
            <span class="stat-card-label">Cancelled</span>
          </div>
        </div>

        <c:if test="${pendingCount > 0}">
          <section class="dashboard-section section-urgent">
            <h2 class="section-title">Needs attention</h2>
            <div class="admin-order-cards">
              <c:forEach var="order" items="${orders}">
                <c:if test="${order.status == 'PENDING'}">
                  <div class="admin-order-card">
                    <div class="admin-order-header">
                      <span class="order-id">Order #<c:out value="${order.id}" /></span>
                      <div class="admin-order-actions">
                        <button class="btn btn-success btn-sm"
                                data-order-id="<c:out value='${order.id}'/>"
                                data-action="complete">Mark complete</button>
                        <button class="btn btn-danger btn-sm"
                                data-order-id="<c:out value='${order.id}'/>"
                                data-action="cancel">Cancel order</button>
                      </div>
                    </div>
                    <div class="admin-order-meta">
                      <span>Customer #<c:out value="${order.userId}" /></span>
                      <span>&euro;<fmt:formatNumber value="${order.totalPrice}" pattern="#,##0.00" /></span>
                      <span><c:out value="${order.dishes.size()}" /> dish<c:if test="${order.dishes.size() != 1}">es</c:if></span>
                    </div>
                  </div>
                </c:if>
              </c:forEach>
            </div>
          </section>
        </c:if>

        <section class="dashboard-section">
          <h2 class="section-title">All Orders</h2>
          <c:choose>
            <c:when test="${empty orders}">
              <p class="empty-state">No orders yet.</p>
            </c:when>
            <c:otherwise>
              <div class="orders-table-wrap">
                <table class="orders-table">
                  <thead>
                    <tr>
                      <th>#</th><th>Customer</th><th>Dishes</th><th>Total</th><th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="order" items="${orders}">
                      <tr>
                        <td class="order-id"><c:out value="${order.id}" /></td>
                        <td>User #<c:out value="${order.userId}" /></td>
                        <td>
                          <div class="order-dish-list">
                            <c:forEach var="dish" items="${order.dishes}">
                              <span class="order-dish-line"><c:out value="${dish.dishName}" /> &times; <c:out value="${dish.quantity}" /></span>
                            </c:forEach>
                          </div>
                        </td>
                        <td class="order-total">&euro;<fmt:formatNumber value="${order.totalPrice}" pattern="#,##0.00" /></td>
                        <td>
                          <c:choose>
                            <c:when test="${order.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                            <c:when test="${order.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                            <c:otherwise><span class="badge badge-cancelled">Cancelled</span></c:otherwise>
                          </c:choose>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:otherwise>
          </c:choose>
        </section>

      </div>
    </main>

    <%@ include file="../fragments/footer.jsp" %>
  </body>
</html>
