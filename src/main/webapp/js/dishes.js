document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector(".navbar")?.dataset.ctx || "";
  const grid = document.querySelector("[data-grid]");
  const status = document.querySelector("[data-status]");
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
    img.classList.add("is-hidden");
    fetch(imageUrl(dish.id), { headers: { Accept: "*/*" } })
      .then((r) => (r.ok ? r.blob() : Promise.reject()))
      .then((blob) => {
        img.src = URL.createObjectURL(blob);
        img.classList.remove("is-hidden");
        placeholder.classList.add("is-hidden");
      })
      .catch(() => {
        img.classList.add("is-hidden");
        placeholder.classList.remove("is-hidden");
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
    description.textContent = dish.description || "No description available for this dish.";

    const footer = document.createElement("footer");
    footer.className = "dish-footer";

    const price = document.createElement("span");
    price.className = "dish-price";
    price.textContent = typeof dish.price === "number" ? money.format(dish.price) : "-";

    footer.appendChild(price);
    body.append(category, name, description, footer);
    link.append(media, body);
    article.appendChild(link);
    return article;
  };

  const render = (dishes) => {
    grid.replaceChildren();
    if (!dishes.length) {
      const q = searchInput ? searchInput.value.trim() : "";
      setStatus(q ? "No dishes match your search." : "No dishes available.");
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
