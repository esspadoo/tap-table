<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Ingredients - TapTable</title>
</head>
<body>
  <h1>Ingredients</h1>

  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Allergens</th>
        <th>Frozen</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="i" items="${ingredients}">
        <tr>
          <td><c:out value="${i.id}" /></td>
          <td><c:out value="${i.name}" /></td>
          <td>
            <c:choose>
              <c:when test="${empty i.allergens}">-</c:when>
              <c:otherwise>
                <c:forEach var="a" items="${i.allergens}" varStatus="s">
                  <c:out value="${a}" /><c:if test="${!s.last}">, </c:if>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </td>
          <td><c:out value="${i.frozen}" /></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>
