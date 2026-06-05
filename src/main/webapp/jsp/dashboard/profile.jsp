<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Profile &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../fragments/navbar.jsp" %>

    <main id="profile-page" class="dash-page">
      <%@ include file="../fragments/dash-tabs.jsp" %>

      <div class="dash-content">
        <div class="dash-page-header">
          <h1>Profile</h1>
        </div>

        <section class="dashboard-section">
          <h2 class="section-title">Account information</h2>
          <div class="profile-fields">
            <div class="profile-field">
              <span class="profile-field-label">Username</span>
              <span class="profile-field-value"
                ><c:out value="${user.username}"
              /></span>
            </div>
            <div class="profile-field">
              <span class="profile-field-label">Full name</span>
              <span class="profile-field-value"
                ><c:out value="${user.name} ${user.surname}"
              /></span>
            </div>
            <div class="profile-field">
              <span class="profile-field-label">Email</span>
              <span class="profile-field-value"
                ><c:out value="${user.email}"
              /></span>
            </div>
            <div class="profile-field">
              <span class="profile-field-label">Role</span>
              <span class="profile-field-value">
                <c:out value="${user_role}" />
              </span>
            </div>
          </div>
        </section>
      </div>
    </main>

    <%@ include file="../fragments/footer.jsp" %>
  </body>
</html>
