import {
  getCart,
  removeDish,
  increaseQuantity,
  decreaseQuantity,
  clearCart,
  getTotalPrice,
  getPromotion,
  savePromotion,
  removePromotion,
  getFinalPrice,
} from "./cart.js";

const ctx = document.querySelector(".navbar")?.dataset.ctx || "";
const money = new Intl.NumberFormat("it-IT", { style: "currency", currency: "EUR" });

const emptyEl = document.getElementById("cart-empty");
const contentEl = document.getElementById("cart-content");
const tbody = document.getElementById("cart-tbody");
const totalEl = document.getElementById("cart-total-price");
const alertEl = document.getElementById("cart-alert");
const checkoutBtn = document.getElementById("checkout-btn");
const clearBtn = document.getElementById("clear-cart-btn");

// Promotion elements
const promoInput = document.getElementById("promo-input");
const promoApplyBtn = document.getElementById("promo-apply-btn");
const promoRemoveBtn = document.getElementById("promo-remove-btn");
const promoFeedback = document.getElementById("promo-feedback");
const promoApplied = document.getElementById("promo-applied");
const promoAppliedCode = document.getElementById("promo-applied-code");
const promoAppliedDiscount = document.getElementById("promo-applied-discount");
const promoOriginalRow = document.getElementById("promo-original-row");
const promoOriginalPrice = document.getElementById("promo-original-price");

function showAlert(message, isError = true) {
  alertEl.textContent = message;
  alertEl.className = "alert " + (isError ? "alert-destructive" : "alert-success");
  alertEl.hidden = false;
}

function hideAlert() {
  alertEl.hidden = true;
}

function setPromoFeedback(message, isError = true) {
  promoFeedback.textContent = message;
  promoFeedback.className = "promo-feedback " + (isError ? "promo-feedback-error" : "promo-feedback-success");
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
    const qtyWrap = document.createElement("div");
    qtyWrap.className = "qty-controls";

    const btnDec = document.createElement("button");
    btnDec.className = "btn btn-outline btn-sm qty-btn";
    btnDec.textContent = "−";
    btnDec.addEventListener("click", () => { decreaseQuantity(item.dishId); render(); });

    const qtySpan = document.createElement("span");
    qtySpan.className = "qty-value";
    qtySpan.textContent = item.quantity;

    const btnInc = document.createElement("button");
    btnInc.className = "btn btn-outline btn-sm qty-btn";
    btnInc.textContent = "+";
    btnInc.addEventListener("click", () => { increaseQuantity(item.dishId); render(); });

    qtyWrap.append(btnDec, qtySpan, btnInc);
    tdQty.appendChild(qtyWrap);

    const tdTotal = document.createElement("td");
    tdTotal.textContent = money.format(item.price * item.quantity);

    const tdRemove = document.createElement("td");
    const removeBtn = document.createElement("button");
    removeBtn.className = "btn btn-danger btn-sm";
    removeBtn.textContent = "Remove";
    removeBtn.addEventListener("click", () => { removeDish(item.dishId); render(); });
    tdRemove.appendChild(removeBtn);

    tr.append(tdName, tdQty, tdTotal, tdRemove);
    tbody.appendChild(tr);
  }

  renderPromoSection();
  totalEl.textContent = money.format(getFinalPrice());
}

// ── Promotion apply ──────────────────────────────────────────

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
    // 1. Validate code exists
    const promoRes = await fetch(ctx + "/rest/promotion/" + encodeURIComponent(code), {
      headers: { Accept: "application/json" },
    });

    if (promoRes.status === 404) {
      setPromoFeedback("Promotion code not found.");
      return;
    }
    if (!promoRes.ok) {
      setPromoFeedback("Could not validate promotion code.");
      return;
    }

    const promo = await promoRes.json();

    // 2. Check if already used
    const usageRes = await fetch(ctx + "/rest/promotion/" + encodeURIComponent(code) + "/usage", {
      headers: { Accept: "application/json" },
    });

    if (usageRes.ok) {
      const usage = await usageRes.json();
      if (usage.is_already_used) {
        setPromoFeedback("This promotion code has already been used.");
        return;
      }
    }

    // 3. Apply
    savePromotion({ code: promo.code, discount: promo.discount, description: promo.description || null });
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

// ── Checkout ─────────────────────────────────────────────────

async function checkout() {
  const cart = getCart();
  if (!cart.length) return;

  checkoutBtn.disabled = true;
  hideAlert();

  let userId;
  try {
    const userRes = await fetch(ctx + "/rest/user", { headers: { Accept: "application/json" } });
    if (userRes.status === 401) {
      showAlert("You must be logged in to place an order.");
      setTimeout(() => { window.location.href = ctx + "/login"; }, 1500);
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
      setTimeout(() => { window.location.href = ctx + "/"; }, 2000);
      return;
    }

    if (res.status === 401) {
      showAlert("You must be logged in to place an order.");
      setTimeout(() => { window.location.href = ctx + "/login"; }, 1500);
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
