document.addEventListener("DOMContentLoaded", () => {
  const navbar = document.querySelector(".navbar");
  const burger = document.querySelector(".burger-button");

  burger.addEventListener("click", () => {
    const expanded = burger.getAttribute("aria-expanded") === "true";
    burger.setAttribute("aria-expanded", String(!expanded));
    navbar.classList.toggle("close");
  });

  const badge = document.getElementById("cart-count");
  if (badge) {
    function syncCartBadge() {
      try {
        const cart = JSON.parse(localStorage.getItem("cart")) || [];
        badge.textContent = cart.reduce((s, i) => s + i.quantity, 0);
      } catch {
        badge.textContent = 0;
      }
    }
    syncCartBadge();
    window.addEventListener("storage", syncCartBadge);
  }
});
