import {
  getCart,
  removeDish,
  increaseQuantity,
  decreaseQuantity,
  clearCart,
  getTotalPrice,
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

function showAlert(message, isError = true) {
  alertEl.textContent = message;
  alertEl.className = "alert " + (isError ? "alert-destructive" : "alert-success");
  alertEl.hidden = false;
}

function hideAlert() {
  alertEl.hidden = true;
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

    const tdPrice = document.createElement("td");
    tdPrice.textContent = money.format(item.price);

    const tdSubtotal = document.createElement("td");
    tdSubtotal.textContent = money.format(item.price * item.quantity);

    const tdRemove = document.createElement("td");
    const removeBtn = document.createElement("button");
    removeBtn.className = "btn btn-danger btn-sm";
    removeBtn.textContent = "Remove";
    removeBtn.addEventListener("click", () => { removeDish(item.dishId); render(); });
    tdRemove.appendChild(removeBtn);

    tr.append(tdName, tdQty, tdPrice, tdSubtotal, tdRemove);
    tbody.appendChild(tr);
  }

  totalEl.textContent = money.format(getTotalPrice());
}

async function checkout() {
  const cart = getCart();
  if (!cart.length) return;

  checkoutBtn.disabled = true;
  hideAlert();

  let userId;
  try {
    const userRes = await fetch(ctx + "/rest/user", { credentials: "include" });
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

  const payload = {
    status: "PENDING",
    user_id: userId,
    promotion_id: null,
    dishes: cart.map((item) => ({
      dish_id: item.dishId,
      quantity: item.quantity,
      is_liked: null,
    })),
  };

  try {
    const res = await fetch(ctx + "/rest/order", {
      method: "POST",
      credentials: "include",
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
