<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Change Password &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/account-password.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main id="password-page" class="dash-page">

      <%@ include file="../fragments/dash-tabs.jsp" %>

      <div class="dash-content">

        <div class="dash-page-header">
          <h1>Change password</h1>
          <p>Choose a strong password to keep your account secure.</p>
        </div>

        <c:if test="${not empty error}">
          <div class="alert alert-destructive">
            <span><c:out value="${error}" /></span>
          </div>
        </c:if>

        <c:if test="${not empty success}">
          <div class="alert alert-success">
            <span><c:out value="${success}" /></span>
          </div>
        </c:if>

        <div class="form-card">
          <div class="form-section">
            <h2>Update password</h2>

            <form id="password-form" class="form" method="post"
                  action="<c:url value='/dashboard/password'/>">
              <div class="form-group">
                <label for="current_password" class="required">Current password</label>
                <input
                  type="password"
                  id="current_password"
                  name="current_password"
                  autocomplete="current-password"
                  required
                />
              </div>

              <div class="form-group">
                <label for="new_password" class="required">New password</label>
                <input
                  type="password"
                  id="new_password"
                  name="new_password"
                  autocomplete="new-password"
                  required
                />
                <div class="password-rules">
                  <span class="password-rule" id="rule-length">At least 8 characters</span>
                </div>
              </div>

              <div class="form-group">
                <label for="confirm_password" class="required">Confirm new password</label>
                <input
                  type="password"
                  id="confirm_password"
                  name="confirm_password"
                  autocomplete="new-password"
                  required
                />
              </div>

              <div class="form-actions">
                <button type="submit" class="btn btn-primary">Change password</button>
              </div>
            </form>
          </div>
        </div>

      </div>
    </main>

    <%@ include file="../fragments/footer.jsp" %>
  </body>
</html>
