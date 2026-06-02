document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const grid = document.getElementById("dishes-grid");
  const status = document.getElementById("dishes-status");

  const setStatus = (message, error = false) => {
    status.textContent = message;
    status.classList.toggle("is-error", error);
  };

  const price = (dish) => {
    if (dish.price == null) return "";
    return "&euro;" + dish.price.toFixed(2).replace(".", ",");
  };

  const card = (dish) => {
    const article = document.createElement("article");
    article.className = "dish-card";

    const name = document.createElement("h3");
    name.className = "dish-card-name";
    name.textContent = dish.name || "Unnamed dish";

    const category = document.createElement("span");
    category.className = "dish-card-category";
    if (dish.category) category.textContent = dish.category;

    const priceEl = document.createElement("span");
    priceEl.className = "dish-card-price";
    priceEl.innerHTML = price(dish);

    article.append(name);
    if (dish.category) article.append(category);
    article.append(priceEl);

    return article;
  };

  const render = (dishes) => {
    grid.replaceChildren();
    if (!dishes.length) {
      setStatus("No dishes available.");
      return;
    }

    const fragment = document.createDocumentFragment();
    for (const dish of dishes) fragment.appendChild(card(dish));
    grid.appendChild(fragment);
    setStatus(`${dishes.length} dishes`);
  };

  fetch(ctx + "/rest/dish", { headers: { Accept: "application/json" } })
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((payload) => {
      const dishes = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      render(dishes);
    })
    .catch((error) => {
      console.error("Failed to load dishes", error);
      setStatus("Failed to load dishes.", true);
    });
});