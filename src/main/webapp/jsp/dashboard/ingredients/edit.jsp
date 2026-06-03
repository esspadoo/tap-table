<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Edit Ingredient &dash; TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" defer></script>
    <script src="<c:url value='/js/ingredients-edit.js'/>" defer></script>
  </head>
  <body data-ctx="<c:out value='${pageContext.request.contextPath}'/>">
    <%@ include file="../../fragments/navbar.jsp" %>

    <main id="ingredient-edit-page">
      <div class="dashboard-header">
        <nav id="breadcrumb" aria-label="Breadcrumb">
          <ol>
            <li><a href="<c:url value='/dashboard'/>">Dashboard</a></li>
            <li><span aria-current="page">Edit Ingredient</span></li>
          </ol>
        </nav>
        <h1 id="page-title">Edit Ingredient</h1>
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
                <img id="image-preview" alt="Ingredient preview" />
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
                    placeholder="e.g. San Marzano Tomato"
                    autocomplete="off"
                    required
                  />
                </div>

                <div class="form-group">
                  <label>Storage</label>
                  <label class="toggle-field" for="is_frozen">
                    <input
                      type="checkbox"
                      id="is_frozen"
                      name="is_frozen"
                      value="true"
                    />
                    <span class="toggle-track">
                      <span class="toggle-thumb"></span>
                    </span>
                    <span class="toggle-label">Frozen ingredient</span>
                  </label>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h2>Allergens</h2>
              <p class="section-hint">
                Select all that apply. Leave empty if none.
              </p>

              <div class="chip-group">
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-cereals"
                    name="allergens"
                    value="cereals"
                  />
                  <label for="a-cereals">Cereals</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-crustaceans"
                    name="allergens"
                    value="crustaceans"
                  />
                  <label for="a-crustaceans">Crustaceans</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-eggs"
                    name="allergens"
                    value="eggs"
                  />
                  <label for="a-eggs">Eggs</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-fish"
                    name="allergens"
                    value="fish"
                  />
                  <label for="a-fish">Fish</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-peanuts"
                    name="allergens"
                    value="peanuts"
                  />
                  <label for="a-peanuts">Peanuts</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-soybeans"
                    name="allergens"
                    value="soybeans"
                  />
                  <label for="a-soybeans">Soybeans</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-milk"
                    name="allergens"
                    value="milk"
                  />
                  <label for="a-milk">Milk</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-nuts"
                    name="allergens"
                    value="nuts"
                  />
                  <label for="a-nuts">Nuts</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-celery"
                    name="allergens"
                    value="celery"
                  />
                  <label for="a-celery">Celery</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-mustard"
                    name="allergens"
                    value="mustard"
                  />
                  <label for="a-mustard">Mustard</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-sesame"
                    name="allergens"
                    value="sesame_seeds"
                  />
                  <label for="a-sesame">Sesame</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-sulphites"
                    name="allergens"
                    value="sulphur_dioxide_and_sulphites"
                  />
                  <label for="a-sulphites">Sulphites</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-lupin"
                    name="allergens"
                    value="lupin"
                  />
                  <label for="a-lupin">Lupin</label>
                </div>
                <div class="chip">
                  <input
                    type="checkbox"
                    id="a-molluscs"
                    name="allergens"
                    value="molluscs"
                  />
                  <label for="a-molluscs">Molluscs</label>
                </div>
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
