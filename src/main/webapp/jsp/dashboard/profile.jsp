<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Account &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/account-password.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main id="profile-page">
      <%@ include file="../fragments/dash-tabs.jsp" %>

      <div class="dash-content">
        <div class="dash-page-header">
          <h1>Account</h1>
        </div>

        <section class="dashboard-section">
          <h2 class="section-title">Account information</h2>
          <div class="profile-fields">
            <div class="profile-field">
              <span class="profile-field-label">Username</span>
              <span class="profile-field-value"><c:out value="${user.username}"/></span>
            </div>
            <div class="profile-field">
              <span class="profile-field-label">Full name</span>
              <span class="profile-field-value"><c:out value="${user.name} ${user.surname}"/></span>
            </div>
            <div class="profile-field">
              <span class="profile-field-label">Email</span>
              <span class="profile-field-value"><c:out value="${user.email}"/></span>
            </div>
            <div class="profile-field">
              <span class="profile-field-label">Role</span>
              <span class="profile-field-value"><c:out value="${user_role}"/></span>
            </div>
          </div>
        </section>

        <section class="dashboard-section">
          <h2 class="section-title">Change password</h2>
          <p class="dash-section-desc">Choose a strong password to keep your account secure.</p>

          <c:if test="${not empty error}">
            <div class="alert alert-destructive">
              <span><c:out value="${error}" /></span>
            </div>
          </c:if>

          <c:if test="${not empty success}">
            <div class="alert alert-success">
              <span>Password changed successfully.</span>
            </div>
          </c:if>

          <form id="password-form" class="form" method="post"
                action="<c:url value='/dashboard/profile'/>">
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
        </section>

      </div>
    </main>

    <%@ include file="../fragments/footer.jsp" %>
  </body>
</html>
