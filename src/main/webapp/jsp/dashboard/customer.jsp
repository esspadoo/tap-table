<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/dashboard.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main id="customer-dashboard" class="dash-page" data-ctx="<c:out value='${pageContext.request.contextPath}'/>">

      <%@ include file="../fragments/dash-tabs.jsp" %>

      <div class="dash-content">

        <div class="dash-page-header">
          <h1>Overview</h1>
          <p>Welcome, <c:out value="${user.name}" /></p>
        </div>

        <section class="dashboard-section">
          <h2 class="section-title">My Orders</h2>
          <c:choose>
            <c:when test="${empty orders}">
              <p class="empty-state">No orders yet.</p>
            </c:when>
            <c:otherwise>
              <div class="orders-list">
                <c:forEach var="order" items="${orders}">
                  <div class="order-card">
                    <header>
                      <span class="order-id">Order #<c:out value="${order.id}" /></span>
                      <c:choose>
                        <c:when test="${order.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                        <c:when test="${order.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                        <c:otherwise><span class="badge badge-cancelled">Cancelled</span></c:otherwise>
                      </c:choose>
                    </header>
                    <div class="order-dishes">
                      <c:forEach var="dish" items="${order.dishes}">
                        <div class="dish-row">
                          <span class="dish-name">Dish #<c:out value="${dish.dishId}" /></span>
                          <span>x<c:out value="${dish.quantity}" /></span>
                          <c:if test="${order.status == 'COMPLETED'}">
                            <c:choose>
                              <c:when test="${dish.isLiked() == true}">
                                <span class="dish-eval dish-eval-liked">&#128077;</span>
                              </c:when>
                              <c:when test="${dish.isLiked() != null}">
                                <span class="dish-eval dish-eval-disliked">&#128078;</span>
                              </c:when>
                              <c:otherwise>
                                <span class="dish-eval"
                                      data-needs-review="true"
                                      data-dish-id="<c:out value='${dish.dishId}'/>"
                                      data-order-id="<c:out value='${order.id}'/>"></span>
                              </c:otherwise>
                            </c:choose>
                          </c:if>
                        </div>
                      </c:forEach>
                    </div>
                    <div class="order-card-footer">
                      <span class="order-total">&euro;<fmt:formatNumber value="${order.totalPrice}" pattern="#,##0.00" /></span>
                      <c:if test="${order.status == 'PENDING'}">
                        <span class="order-pending-note">Pay at the counter</span>
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

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
