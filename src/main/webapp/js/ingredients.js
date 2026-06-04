document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector(".navbar")?.dataset.ctx || "";
  const grid = document.getElementById("ingredients-grid");
  const status = document.getElementById("ingredients-status");
  const searchInput = document.getElementById("ingredients-search");
  let allIngredients = [];

  const setStatus = (message, error = false) => {
    status.textContent = message;
    status.classList.toggle("is-error", error);
  };

  const imageUrl = (ingredientId) => `${ctx}/rest/ingredient/${ingredientId}/image`;

  const card = (ingredient) => {
    const article = document.createElement("article");
    article.className = "media-card";

    const link = document.createElement("a");
    link.href = `${ctx}/ingredient?id=${ingredient.id}`;

    const media = document.createElement("div");
    media.className = "media-card-media";

    const placeholder = document.createElement("div");
    placeholder.className = "media-card-placeholder";
    placeholder.setAttribute("aria-hidden", "true");

    const img = document.createElement("img");
    img.alt = `${ingredient.name || "Ingredient"} image`;
    img.loading = "lazy";
    img.decoding = "async";
    img.classList.add("is-hidden");
    fetch(imageUrl(ingredient.id), { headers: { Accept: "*/*" } })
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

    const name = document.createElement("h2");
    name.className = "media-card-name";
    name.textContent = ingredient.name || "Unnamed ingredient";

    body.appendChild(name);

    link.append(media, body);
    article.appendChild(link);
    return article;
  };

  const render = (ingredients) => {
    grid.replaceChildren();
    if (!ingredients.length) {
      const q = searchInput.value.trim();
      setStatus(q ? "No ingredients match your search." : "No ingredients available.");
      return;
    }

    const fragment = document.createDocumentFragment();
    for (const ingredient of ingredients) fragment.appendChild(card(ingredient));
    grid.appendChild(fragment);
    setStatus(`${ingredients.length} ingredient${ingredients.length !== 1 ? "s" : ""}`);
  };

  fetch(ctx + "/rest/ingredient", { headers: { Accept: "application/json" } })
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((payload) => {
      allIngredients = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      render(allIngredients);
    })
    .catch((error) => {
      console.error("Failed to load ingredients", error);
      setStatus("Failed to load ingredients.", true);
    });

  searchInput.addEventListener("input", () => {
    const q = searchInput.value.trim().toLowerCase();
    const filtered = q
      ? allIngredients.filter((i) => (i.name || "").toLowerCase().includes(q))
      : allIngredients;
    render(filtered);
  });
});
