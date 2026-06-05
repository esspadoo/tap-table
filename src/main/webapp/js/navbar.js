import { getTotalItems } from "./cart.js";

const navbar = document.querySelector(".navbar");
const burger = document.querySelector(".burger-button");
const ctx = navbar.dataset.ctx || "";

function syncCartBadge() {
  const badge = document.getElementById("cart-count");
  if (badge) badge.textContent = getTotalItems();
}

syncCartBadge();
window.addEventListener("storage", syncCartBadge);

const link = (href, text) => {
  const a = document.createElement("a");
  a.href = ctx + href;
  a.textContent = text;
  return a;
};

burger.addEventListener("click", () => {
  const expanded = burger.getAttribute("aria-expanded") === "true";
  burger.setAttribute("aria-expanded", String(!expanded));
  navbar.classList.toggle("close");
});

fetch(ctx + "/rest/user", { headers: { Accept: "application/json" } })
  .then((res) => {
    if (!res.ok) return;
    return res.json();
  })
  .then((user) => {
    if (!user) return;

    const items = navbar.querySelector(".items");
    const actions = navbar.querySelector(".actions");

    items.replaceChildren();
    actions.replaceChildren();

    items.appendChild(link("/", "Home"));
    items.appendChild(link("/ingredients", "Ingredients"));

    const cartLink = link("/cart", "");
    cartLink.className = "cart-link";
    const cartLabel = document.createTextNode("Cart (");
    const cartBadge = document.createElement("span");
    cartBadge.id = "cart-count";
    cartBadge.textContent = getTotalItems();
    const cartClose = document.createTextNode(")");
    cartLink.append(cartLabel, cartBadge, cartClose);
    items.appendChild(cartLink);

    const dashboardLink = link("/dashboard", "Dashboard");
    dashboardLink.className = "btn btn-primary btn-md";
    actions.appendChild(dashboardLink);

    const signOut = document.createElement("button");
    signOut.className = "btn btn-outline btn-md";
    signOut.textContent = "Sign out";
    signOut.addEventListener("click", () => {
      signOut.disabled = true;
      fetch(ctx + "/logout", { method: "POST" })
        .then((res) => {
          if (res.ok) {
            location.href = ctx + "/";
          } else {
            signOut.disabled = false;
            signOut.textContent = "Sign out failed - try again";
          }
        })
        .catch(() => {
          signOut.disabled = false;
          signOut.textContent = "Sign out failed - try again";
        });
    });
    actions.appendChild(signOut);
  });
