document.addEventListener("DOMContentLoaded", () => {
  const CART_KEY = "cart";

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
  function addDish(dish) {
    const cart = getCart();
    const existing = cart.find((item) => item.dishId === dish.dishId);
    if (existing) {
      existing.quantity += 1;
    } else {
      cart.push({
        dishId: dish.dishId,
        name: dish.name,
        price: dish.price,
        quantity: 1,
      });
    }
    saveCart(cart);
  }
  function getTotalItems() {
    return getCart().reduce((sum, item) => sum + item.quantity, 0);
  }

  function syncCartBadge() {
    const badge = document.getElementById("cart-count");
    if (badge) badge.textContent = getTotalItems();
  }

  syncCartBadge();
  const ctx = document.getElementById("dishes-page").dataset.ctx;
  const grid = document.getElementById("dishes-grid");
  const status = document.getElementById("dishes-status");
  const searchInput = document.getElementById("dishes-search");
  const money = new Intl.NumberFormat("it-IT", {
    style: "currency",
    currency: "EUR",
  });
  let allDishes = [];

  if (!grid || !status) return;

  const setStatus = (message, error = false) => {
    status.textContent = message;
    status.classList.toggle("is-error", error);
  };

  const imageUrl = (dishId) => `${ctx}/rest/dish/${dishId}/image`;

  const card = (dish) => {
    const article = document.createElement("article");
    article.className = "media-card";

    const link = document.createElement("a");
    link.href = `${ctx}/dish?id=${dish.id}`;

    const media = document.createElement("div");
    media.className = "media-card-media";

    const placeholder = document.createElement("div");
    placeholder.className = "media-card-placeholder";
    placeholder.setAttribute("aria-hidden", "true");

    const img = document.createElement("img");
    img.alt = `${dish.name || "Dish"} image`;
    img.loading = "lazy";
    img.decoding = "async";
    img.hidden = true;
    fetch(imageUrl(dish.id), { headers: { Accept: "*/*" } })
      .then((r) => (r.ok ? r.blob() : Promise.reject()))
      .then((blob) => {
        img.src = URL.createObjectURL(blob);
        img.hidden = false;
        placeholder.hidden = true;
      })
      .catch(() => {
        img.hidden = true;
        placeholder.hidden = false;
      });

    media.append(placeholder, img);

    const body = document.createElement("div");
    body.className = "media-card-body";

    const category = document.createElement("p");
    category.className = "dish-category";
    category.textContent = dish.category || "Uncategorized";

    const name = document.createElement("h2");
    name.className = "media-card-name";
    name.textContent = dish.name || "Unnamed dish";

    const description = document.createElement("p");
    description.className = "dish-description";
    description.textContent =
      dish.description || "No description available for this dish.";

    const footer = document.createElement("footer");
    footer.className = "dish-footer";

    const price = document.createElement("span");
    price.className = "dish-price";
    price.textContent =
      typeof dish.price === "number" ? money.format(dish.price) : "-";

    footer.appendChild(price);
    body.append(category, name, description, footer);
    link.append(media, body);
    article.appendChild(link);

    const addBtn = document.createElement("button");
    addBtn.className = "btn btn-primary btn-sm add-to-cart-btn";
    addBtn.textContent = "Add to cart";
    addBtn.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();
      addDish({ dishId: dish.id, name: dish.name, price: dish.price });
      syncCartBadge();
      addBtn.textContent = "Added!";
      setTimeout(() => {
        addBtn.textContent = "Add to cart";
      }, 1200);
    });
    article.appendChild(addBtn);

    return article;
  };

  const render = (dishes) => {
    grid.replaceChildren();
    if (!dishes.length) {
      setStatus("No dishes available.");
      const empty = document.createElement("p");
      empty.className = "catalog-status";
      empty.textContent = "No dishes available right now.";
      grid.appendChild(empty);
      return;
    }

    const fragment = document.createDocumentFragment();
    for (const dish of dishes) fragment.appendChild(card(dish));
    grid.appendChild(fragment);
    setStatus(`${dishes.length} dish${dishes.length !== 1 ? "es" : ""}`);
  };

  fetch(ctx + "/rest/dish", { headers: { Accept: "application/json" } })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      return response.json();
    })
    .then((payload) => {
      allDishes = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      render(allDishes);
    })
    .catch((error) => {
      console.error("Failed to load dishes", error);
      setStatus("Failed to load dishes.", true);
      grid.replaceChildren();
      const empty = document.createElement("p");
      empty.className = "catalog-status is-error";
      empty.textContent = "The dishes list could not be loaded.";
      grid.appendChild(empty);
    });

  if (searchInput) {
    searchInput.addEventListener("input", () => {
      const q = searchInput.value.trim().toLowerCase();
      const filtered = q
        ? allDishes.filter((d) => (d.name || "").toLowerCase().includes(q))
        : allDishes;
      render(filtered);
    });
  }
});
