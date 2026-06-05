document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector(".navbar")?.dataset.ctx || "";
  const grid = document.querySelector("[data-grid]");
  const status = document.querySelector("[data-status]");
  const money = new Intl.NumberFormat("it-IT", {
    style: "currency",
    currency: "EUR",
  });

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
    link.href = `${ctx}/jsp/dish-detail.jsp?id=${dish.id}`;

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
    setStatus(`${dishes.length} dishes`);
  };

  fetch(ctx + "/rest/dish", { headers: { Accept: "application/json" } })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
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
      grid.replaceChildren();
      const empty = document.createElement("p");
      empty.className = "catalog-status is-error";
      empty.textContent = "The dishes list could not be loaded.";
      grid.appendChild(empty);
    });
});
