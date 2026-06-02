<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>New Dish &mdash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/dish-create.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="dish-create-page">
      <div class="dashboard-header">
        <nav class="breadcrumb" aria-label="breadcrumb">
          <a href="<c:url value='/dashboard'/>">Dashboard</a>
          <span class="breadcrumb-sep">/</span>
          <a href="<c:url value='/dashboard/dish'/>">Dishes</a>
          <span class="breadcrumb-sep">/</span>
          <span>New Dish</span>
        </nav>
        <h1>New Dish</h1>
      </div>

      <div id="alert-container">
        <div class="alert alert-destructive">
          <span id="alert-message"></span>
        </div>
      </div>

      <form id="create-form" novalidate>
        <div class="form-layout">
          <div class="form-card form-card-image">
            <div class="form-section">
              <h2>Image</h2>

              <div id="image-dropzone">
                <input
                  type="file"
                  id="image-input"
                  name="image"
                  accept="image/webp,image/png,image/jpeg"
                />
                <img
                  id="image-preview"
                  alt="Dish preview"
                />
                <div class="dropzone-content">
                  <div class="dropzone-icon">&#x1F35A;</div>
                  <p class="dropzone-hint">Drop image here or click to upload</p>
                  <p class="dropzone-formats">WebP &middot; PNG &middot; JPEG</p>
                </div>
              </div>
            </div>
          </div>

          <div class="form-card">
            <div class="form-section">
              <h2>Details</h2>

              <div class="form">
                <div class="form-group">
                  <label for="name" class="required">Name</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    placeholder="e.g. Margherita"
                    autocomplete="off"
                    required
                  />
                </div>

                <div class="form-group">
                  <label for="description">Description</label>
                  <input
                    type="text"
                    id="description"
                    name="description"
                    placeholder="e.g. Pizza con pomodoro, mozzarella e basilico"
                    autocomplete="off"
                  />
                </div>

                <div class="form-group">
                  <label for="price" class="required">Price (&euro;)</label>
                  <input
                    type="number"
                    id="price"
                    name="price"
                    placeholder="e.g. 8.50"
                    step="0.01"
                    min="0.01"
                    autocomplete="off"
                    required
                  />
                </div>

                <div class="form-group">
                  <label for="category" class="required">Category</label>
                  <select id="category" name="category" required>
                    <option value="">Select a category</option>
                    <option value="Antipasti">Antipasti</option>
                    <option value="Primi">Primi</option>
                    <option value="Secondi">Secondi</option>
                    <option value="Contorni">Contorni</option>
                    <option value="Dolci">Dolci</option>
                    <option value="Bevande">Bevande</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h2>Ingredients</h2>
              <p class="section-hint">
                Select all ingredients used in this dish.
              </p>

              <div id="ingredients-group" class="chip-group">
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <a href="<c:url value='/dashboard/dish'/>" class="btn btn-outline"
            >Cancel</a
          >
          <button type="submit" class="btn btn-primary">
            Create dish
          </button>
        </div>
      </form>
    </main>
  </body>
</html>