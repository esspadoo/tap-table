<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ page
contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Cart - TapTable</title>
    <link rel="stylesheet" href="<c:url value='/css/global.css'/>" />
    <script src="<c:url value='/js/navbar.js'/>" type="module"></script>
    <script src="<c:url value='/js/cart-page.js'/>" type="module"></script>
  </head>
  <body>
    <%@ include file="fragments/navbar.jsp" %>

    <main class="cart-page">
      <h1>Your Cart</h1>

      <div id="cart-empty" class="cart-empty" hidden>
        <p class="cart-empty-msg">Nothing in your cart yet.</p>
        <a class="btn btn-primary" href="<c:url value='/dishes'/>"
          >Browse dishes</a
        >
      </div>

      <div id="cart-content" hidden>
        <div id="cart-alert" class="alert" hidden></div>

        <div class="cart-layout">
          <div class="cart-main">
            <div class="data-table-wrap">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>Dish</th>
                    <th>Qty</th>
                    <th>Total</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody id="cart-tbody"></tbody>
              </table>
            </div>
          </div>

          <aside class="cart-sidebar">
            <div class="promo-section">
              <div class="promo-input-row">
                <div class="promo-input-wrap">
                  <input
                    type="text"
                    id="promo-input"
                    class="promo-input"
                    placeholder="Promo code"
                    autocomplete="off"
                    spellcheck="false"
                    maxlength="20"
                  />
                  <button id="promo-apply-btn" class="btn btn-secondary btn-sm">Apply</button>
                </div>
                <button
                  id="promo-remove-btn"
                  class="btn btn-outline btn-sm"
                  hidden
                >
                  Remove
                </button>
              </div>
              <p id="promo-feedback" class="promo-feedback" hidden></p>
              <div id="promo-applied" class="promo-applied" hidden>
                <span id="promo-applied-code" class="promo-applied-code"></span>
                <span class="promo-applied-sep">&middot;</span>
                <span
                  id="promo-applied-discount"
                  class="promo-applied-discount"
                ></span>
                off
              </div>
            </div>

            <div class="cart-summary">
              <div id="promo-original-row" class="cart-original-total" hidden>
                <span>Subtotal</span>
                <span id="promo-original-price"></span>
              </div>
              <div class="cart-total">
                <span>Total</span>
                <strong id="cart-total-price">0,00 &euro;</strong>
              </div>
            </div>

            <div class="cart-actions">
              <button id="checkout-btn" class="btn btn-primary">
                Place order
              </button>
              <button id="clear-cart-btn" class="btn btn-outline btn-sm">
                Clear cart
              </button>
            </div>
          </aside>
        </div>
      </div>
    </main>

    <%@ include file="fragments/footer.jsp" %>
  </body>
</html>
