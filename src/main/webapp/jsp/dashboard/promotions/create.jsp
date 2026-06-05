<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>New Promotion &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/promotions-create.js'/>" defer></script>
  </head>
  <body>
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="promotion-create-page" data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
      <div class="dash-page-header">
        <nav id="breadcrumb" aria-label="Breadcrumb">
          <ol>
            <li><a href="<c:url value='/dashboard'/>">Dashboard</a></li>
            <li><a href="<c:url value='/dashboard/promotions'/>">Promotions</a></li>
            <li><span aria-current="page">New Promotion</span></li>
          </ol>
        </nav>
        <h1>New Promotion</h1>
      </div>

      <div id="alert-container">
        <div class="alert alert-destructive">
          <span id="alert-message"></span>
        </div>
      </div>

      <form id="create-form" novalidate>
        <div class="form-layout">
          <div class="form-card">
            <div class="form-section">
              <h2>Details</h2>

              <div class="form">
                <div class="form-row">
                  <div class="form-group">
                    <label for="code" class="required">Code</label>
                    <input
                      type="text"
                      id="code"
                      name="code"
                      placeholder="e.g. SUMMER25"
                      autocomplete="off"
                      required
                    />
                  </div>
                  <div class="form-group">
                    <label for="discount" class="required">Discount (%)</label>
                    <input
                      type="number"
                      id="discount"
                      name="discount"
                      placeholder="e.g. 15"
                      min="0"
                      max="100"
                      step="0.01"
                      autocomplete="off"
                      required
                    />
                  </div>
                </div>

                <div class="form-group">
                  <label for="description">Description</label>
                  <textarea
                    id="description"
                    name="description"
                    placeholder="e.g. 15% off all orders in summer"
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label for="valid_from" class="required">Valid from</label>
                    <input
                      type="datetime-local"
                      id="valid_from"
                      name="valid_from"
                      required
                    />
                  </div>
                  <div class="form-group">
                    <label for="valid_to" class="required">Valid to</label>
                    <input
                      type="datetime-local"
                      id="valid_to"
                      name="valid_to"
                      required
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <a href="<c:url value='/dashboard/promotions'/>" class="btn btn-outline"
            >Cancel</a
          >
          <button type="submit" class="btn btn-primary">Create promotion</button>
        </div>
      </form>
    </main>

    <footer class="footer">
      <p>&copy; 2026 TapTable &dash; University of Padua</p>
    </footer>
  </body>
</html>
