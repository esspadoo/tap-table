<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib
prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard - TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/dashboard.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main
      class="dashboard"
      data-ctx="<c:out value='${pageContext.request.contextPath}'/>"
    >
      <div class="dashboard-header">
        <h1>Welcome, <c:out value="${user.name}" /></h1>
        <p><c:out value="${user.email}" /></p>
      </div>

      <c:choose>
        <c:when test="${user.role == 'CUSTOMER'}">
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
                        <span class="order-id"
                          >Order #<c:out value="${order.id}"
                        /></span>
                        <c:choose>
                          <c:when test="${order.status == 'PENDING'}">
                            <span class="badge badge-pending">Pending</span>
                          </c:when>
                          <c:when test="${order.status == 'COMPLETED'}">
                            <span class="badge badge-completed">Completed</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge badge-cancelled">Cancelled</span>
                          </c:otherwise>
                        </c:choose>
                      </header>

                      <div class="order-dishes">
                        <c:forEach var="dish" items="${order.dishes}">
                          <div class="dish-row">
                            <span class="dish-name"
                              >Dish #<c:out value="${dish.dishId}"
                            /></span>
                            <span
                              >x<c:out value="${dish.quantity}"
                            /></span>
                            <c:if test="${order.status == 'COMPLETED'}">
                              <span class="dish-eval">
                                <c:choose>
                                  <c:when test="${dish.liked == true}"
                                    >&#128077;</c:when
                                  >
                                  <c:when test="${dish.liked == false}"
                                    >&#128078;</c:when
                                  >
                                  <c:otherwise>&dash;</c:otherwise>
                                </c:choose>
                              </span>
                            </c:if>
                          </div>
                        </c:forEach>
                      </div>

                      <div class="order-card-footer">
                        <span class="order-total">
                          &euro;<fmt:formatNumber
                            value="${order.totalPrice}"
                            pattern="#,##0.00"
                          />
                        </span>
                        <c:if test="${order.status == 'PENDING'}">
                          <span class="order-pending-note"
                            >Pay at the counter</span
                          >
                        </c:if>
                      </div>
                    </div>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>
          </section>
        </c:when>

        <c:otherwise>
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
                        <th>#</th>
                        <th>User</th>
                        <th>Dishes</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="order" items="${orders}">
                        <tr>
                          <td class="order-id">
                            <c:out value="${order.id}" />
                          </td>
                          <td>User #<c:out value="${order.userId}" /></td>
                          <td>
                            <c:forEach
                              var="dish"
                              items="${order.dishes}"
                              varStatus="loop"
                            >
                              Dish #<c:out value="${dish.dishId}" /> x<c:out
                                value="${dish.quantity}"
                              /><c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                          </td>
                          <td class="order-total">
                            &euro;<fmt:formatNumber
                              value="${order.totalPrice}"
                              pattern="#,##0.00"
                            />
                          </td>
                          <td>
                            <c:choose>
                              <c:when test="${order.status == 'PENDING'}">
                                <span class="badge badge-pending">Pending</span>
                              </c:when>
                              <c:when test="${order.status == 'COMPLETED'}">
                                <span class="badge badge-completed"
                                  >Completed</span
                                >
                              </c:when>
                              <c:otherwise>
                                <span class="badge badge-cancelled"
                                  >Cancelled</span
                                >
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td>
                            <c:if test="${order.status == 'PENDING'}">
                              <button
                                class="btn btn-secondary"
                                data-order-id="<c:out value='${order.id}'/>"
                                data-action="complete"
                              >
                                Mark complete
                              </button>
                            </c:if>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
              </c:otherwise>
            </c:choose>
          </section>
        </c:otherwise>
      </c:choose>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
