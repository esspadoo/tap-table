document.addEventListener("DOMContentLoaded", () => {
  const CART_KEY = "cart";
  const PROMO_KEY = "cart_promotion";

  function getCart() {
    try {
      return JSON.parse(localStorage.getItem(CART_KEY)) || [];
    } catch {
      return [];
    }
  }
  function saveCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
  }
  function removeDish(dishId) {
    saveCart(getCart().filter((item) => item.dishId !== dishId));
  }
  function setQuantity(dishId, quantity) {
    if (quantity < 1) {
      removeDish(dishId);
      return;
    }
    const cart = getCart();
    const item = cart.find((i) => i.dishId === dishId);
    if (item) {
      item.quantity = quantity;
      saveCart(cart);
    }
  }
  function clearCart() {
    localStorage.removeItem(CART_KEY);
    localStorage.removeItem(PROMO_KEY);
  }
  function getTotalPrice() {
    return getCart().reduce((sum, item) => sum + item.price * item.quantity, 0);
  }
  function getPromotion() {
    try {
      return JSON.parse(localStorage.getItem(PROMO_KEY)) || null;
    } catch {
      return null;
    }
  }
  function savePromotion(promo) {
    localStorage.setItem(PROMO_KEY, JSON.stringify(promo));
  }
  function removePromotion() {
    localStorage.removeItem(PROMO_KEY);
  }
  function getFinalPrice() {
    const base = getTotalPrice();
    const promo = getPromotion();
    if (!promo) return base;
    return base - (base * promo.discount) / 100;
  }

  const ctx = document.getElementById("cart-page").dataset.ctx;
  const money = new Intl.NumberFormat("it-IT", {
    style: "currency",
    currency: "EUR",
  });

  const emptyEl = document.getElementById("cart-empty");
  const contentEl = document.getElementById("cart-content");
  const tbody = document.getElementById("cart-tbody");
  const totalEl = document.getElementById("cart-total-price");
  const alertEl = document.getElementById("cart-alert");
  const checkoutBtn = document.getElementById("checkout-btn");
  const clearBtn = document.getElementById("clear-cart-btn");

  const promoInput = document.getElementById("promo-input");
  const promoApplyBtn = document.getElementById("promo-apply-btn");
  const promoRemoveBtn = document.getElementById("promo-remove-btn");
  const promoFeedback = document.getElementById("promo-feedback");
  const promoApplied = document.getElementById("promo-applied");
  const promoAppliedCode = document.getElementById("promo-applied-code");
  const promoAppliedDiscount = document.getElementById(
    "promo-applied-discount",
  );
  const promoOriginalRow = document.getElementById("promo-original-row");
  const promoOriginalPrice = document.getElementById("promo-original-price");

  function showAlert(message, isError = true) {
    alertEl.textContent = message;
    alertEl.className =
      "alert " + (isError ? "alert-destructive" : "alert-success");
    alertEl.hidden = false;
  }

  function hideAlert() {
    alertEl.hidden = true;
  }

  function setPromoFeedback(message, isError = true) {
    promoFeedback.textContent = message;
    promoFeedback.className =
      "promo-feedback " +
      (isError ? "promo-feedback-error" : "promo-feedback-success");
    promoFeedback.hidden = !message;
  }

  function renderPromoSection() {
    const promo = getPromotion();
    if (promo) {
      promoInput.value = "";
      promoInput.disabled = true;
      promoApplyBtn.disabled = true;
      promoApplied.hidden = false;
      promoAppliedCode.textContent = promo.code;
      promoAppliedDiscount.textContent = promo.discount + "%";
      promoRemoveBtn.hidden = false;
      promoOriginalRow.hidden = false;
      promoOriginalPrice.textContent = money.format(getTotalPrice());
    } else {
      promoInput.disabled = false;
      promoApplyBtn.disabled = false;
      promoApplied.hidden = true;
      promoRemoveBtn.hidden = true;
      promoOriginalRow.hidden = true;
    }
  }

  function render() {
    const cart = getCart();
    const badge = document.getElementById("cart-count");
    if (badge) badge.textContent = cart.reduce((s, i) => s + i.quantity, 0);
    tbody.replaceChildren();
    hideAlert();

    if (!cart.length) {
      emptyEl.hidden = false;
      contentEl.hidden = true;
      return;
    }

    emptyEl.hidden = true;
    contentEl.hidden = false;

    for (const item of cart) {
      const tr = document.createElement("tr");

      const tdName = document.createElement("td");
      tdName.textContent = item.name;

      const tdQty = document.createElement("td");
      const qtyInput = document.createElement("input");
      qtyInput.type = "number";
      qtyInput.min = "1";
      qtyInput.value = String(item.quantity);
      qtyInput.className = "qty-input";
      qtyInput.setAttribute("aria-label", "Quantity of " + item.name);
      qtyInput.addEventListener("change", () => {
        const val = parseInt(qtyInput.value, 10);
        if (!isNaN(val)) {
          setQuantity(item.dishId, val);
          render();
        }
      });
      tdQty.appendChild(qtyInput);

      const tdTotal = document.createElement("td");
      tdTotal.textContent = money.format(item.price * item.quantity);

      const tdRemove = document.createElement("td");
      const removeBtn = document.createElement("button");
      removeBtn.className = "btn btn-danger btn-sm";
      removeBtn.textContent = "Remove";
      removeBtn.setAttribute(
        "aria-label",
        "Remove " + item.name + " from cart",
      );
      removeBtn.addEventListener("click", () => {
        removeDish(item.dishId);
        render();
      });
      tdRemove.appendChild(removeBtn);

      tr.append(tdName, tdQty, tdTotal, tdRemove);
      tbody.appendChild(tr);
    }

    renderPromoSection();
    totalEl.textContent = money.format(getFinalPrice());
  }

  async function applyPromotion() {
    const code = promoInput.value.trim().toUpperCase();
    if (!code) {
      setPromoFeedback("Enter a promotion code.");
      return;
    }

    promoApplyBtn.disabled = true;
    promoApplyBtn.textContent = "Checking…";
    setPromoFeedback("");

    try {
      const promoRes = await fetch(
        ctx + "/rest/promotion/" + encodeURIComponent(code),
        {
          headers: { Accept: "application/json" },
        },
      );

      if (promoRes.status === 404) {
        setPromoFeedback("Promotion code not found.");
        return;
      }
      if (!promoRes.ok) {
        setPromoFeedback("Could not validate promotion code.");
        return;
      }

      const promo = await promoRes.json();

      const usageRes = await fetch(
        ctx + "/rest/promotion/" + encodeURIComponent(code) + "/usage",
        {
          headers: { Accept: "application/json" },
        },
      );

      if (usageRes.ok) {
        const usage = await usageRes.json();
        if (usage.is_already_used) {
          setPromoFeedback("This promotion code has already been used.");
          return;
        }
      }

      savePromotion({
        code: promo.code,
        discount: promo.discount,
        description: promo.description || null,
      });
      setPromoFeedback("Promotion applied!", false);
      render();
    } catch {
      setPromoFeedback("Could not validate promotion code.");
    } finally {
      promoApplyBtn.disabled = false;
      promoApplyBtn.textContent = "Apply";
    }
  }

  promoApplyBtn.addEventListener("click", applyPromotion);
  promoInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter") applyPromotion();
  });
  promoRemoveBtn.addEventListener("click", () => {
    removePromotion();
    promoInput.value = "";
    setPromoFeedback("");
    render();
  });

  async function checkout() {
    const cart = getCart();
    if (!cart.length) return;

    checkoutBtn.disabled = true;
    hideAlert();

    let userId;
    try {
      const userRes = await fetch(ctx + "/rest/user", {
        headers: { Accept: "application/json" },
      });
      if (userRes.status === 401) {
        showAlert("You must be logged in to place an order.");
        setTimeout(() => {
          window.location.href = ctx + "/login";
        }, 1500);
        return;
      }
      if (!userRes.ok) throw new Error("Failed to fetch user");
      const user = await userRes.json();
      userId = user.id;
    } catch {
      showAlert("Could not verify your session. Please log in again.");
      checkoutBtn.disabled = false;
      return;
    }

    const promo = getPromotion();
    const payload = {
      status: "PENDING",
      user_id: userId,
      promotion_code: promo ? promo.code : null,
      dishes: cart.map((item) => ({
        dish_id: item.dishId,
        quantity: item.quantity,
        is_liked: null,
      })),
    };

    try {
      const res = await fetch(ctx + "/rest/order", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (res.status === 201) {
        clearCart();
        render();
        showAlert("Order created successfully.", false);
        setTimeout(() => {
          window.location.href = ctx + "/";
        }, 2000);
        return;
      }
      if (res.status === 401) {
        showAlert("You must be logged in to place an order.");
        setTimeout(() => {
          window.location.href = ctx + "/login";
        }, 1500);
        return;
      }
      if (res.status === 400) {
        let msg = "Invalid order.";
        try {
          const body = await res.json();
          if (body.message) msg = body.message;
        } catch {}
        showAlert(msg);
        return;
      }
      showAlert("Unexpected server error.");
    } catch {
      showAlert("Unexpected server error.");
    } finally {
      checkoutBtn.disabled = false;
    }
  }

  clearBtn.addEventListener("click", () => {
    clearCart();
    render();
  });
  checkoutBtn.addEventListener("click", checkout);

  render();
});
