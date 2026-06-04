<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Edit Dish &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/dishes-edit.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="dish-edit-page">
      <div class="dashboard-header">
        <nav id="breadcrumb" aria-label="Breadcrumb">
          <ol>
            <li><a href="<c:url value='/dashboard'/>">Dashboard</a></li>
            <li><span aria-current="page">Edit Dish</span></li>
          </ol>
        </nav>
        <h1 id="page-title">Edit Dish</h1>
      </div>

      <div id="alert-container">
        <div class="alert alert-destructive">
          <span id="alert-message"></span>
        </div>
      </div>

      <form id="edit-form" novalidate>
        <div id="form-loading" aria-hidden="true">
          <div class="skeleton-row skeleton-wide"></div>
          <div class="skeleton-row skeleton-narrow"></div>
          <div class="skeleton-row skeleton-mid"></div>
        </div>

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
                <img id="image-preview" alt="Dish preview" />
                <div class="dropzone-content">
                  <div class="dropzone-icon">&#x1F5BC;</div>
                  <p class="dropzone-hint">
                    Drop image here or click to upload
                  </p>
                  <p class="dropzone-formats">
                    WebP &middot; PNG &middot; JPEG
                  </p>
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
                    placeholder="e.g. Margherita Pizza"
                    autocomplete="off"
                    required
                  />
                </div>

                <div class="form-group">
                  <label for="description">Description</label>
                  <textarea
                    id="description"
                    name="description"
                    placeholder="e.g. Classic tomato and mozzarella"
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label for="price">Price (&euro;)</label>
                  <input
                    type="number"
                    id="price"
                    name="price"
                    placeholder="e.g. 9.90"
                    min="0"
                    step="0.01"
                    autocomplete="off"
                  />
                </div>

                <div class="form-group">
                  <label for="category">Category</label>
                  <select id="category" name="category">
                    <option value="">— select —</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h2>Ingredients</h2>
              <p class="section-hint">
                Select all that apply. Leave empty if none.
              </p>

              <div class="search-wrap ingredients-search-wrap">
                <span class="search-icon">&#x1F50E;</span>
                <input
                  type="search"
                  id="ingredients-search"
                  placeholder="Search ingredients&hellip;"
                  autocomplete="off"
                />
              </div>
              <div class="chip-group" id="ingredients-chip-group">
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <a href="<c:url value='/dashboard'/>" class="btn btn-outline"
            >Cancel</a
          >
          <button type="submit" class="btn btn-primary">Save changes</button>
        </div>
      </form>
    </main>
  </body>
</html>
