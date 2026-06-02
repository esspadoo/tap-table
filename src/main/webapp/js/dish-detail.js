document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector(".navbar")?.dataset.ctx || "";
  const img = document.getElementById("dish-image");
  const placeholder = document.getElementById("dish-image-placeholder");
  const category = document.getElementById("dish-category");
  const name = document.getElementById("dish-name");
  const description = document.getElementById("dish-description");
  const price = document.getElementById("dish-price");
  const ingredientsSection = document.getElementById(
    "dish-ingredients-section",
  );
  const ingredientsList = document.getElementById("ingredients-list");

  ingredientsSection.classList.add("is-hidden");

  const params = new URLSearchParams(window.location.search);
  const dishId = params.get("id");

  if (!dishId) return;

  const money = new Intl.NumberFormat("it-IT", {
    style: "currency",
    currency: "EUR",
  });

  const renderIngredients = (ingredients) => {
    ingredientsList.replaceChildren();
    for (const ing of ingredients) {
      const li = document.createElement("li");
      li.className = "ingredient-item";

      if (ing.is_frozen === true) {
        const frozenSpan = document.createElement("span");
        frozenSpan.className = "ingredient-frozen";
        frozenSpan.innerHTML = `<svg role="img" aria-label="frozen" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m10 20-1.25-2.5L6 18"/><path d="M10 4 8.75 6.5 6 6"/><path d="m14 20 1.25-2.5L18 18"/><path d="m14 4 1.25 2.5L18 6"/><path d="m17 21-3-6h-4"/><path d="m17 3-3 6 1.5 3"/><path d="M2 12h6.5L10 9"/><path d="m20 10-1.5 2 1.5 2"/><path d="M22 12h-6.5L14 15"/><path d="m4 10 1.5 2L4 14"/><path d="m7 21 3-6-1.5-3"/><path d="m7 3 3 6h4"/></svg>`;
        li.appendChild(frozenSpan);
      }

      const nameSpan = document.createElement("span");
      nameSpan.className = "ingredient-name";
      nameSpan.textContent = ing.name || "Unknown ingredient";
      li.appendChild(nameSpan);

      if (ing.allergens && ing.allergens.length) {
        const allergensSpan = document.createElement("span");
        allergensSpan.className = "ingredient-allergens";
        allergensSpan.textContent = `allergens: ${ing.allergens.join(", ")}`;
        li.appendChild(allergensSpan);
      }

      ingredientsList.appendChild(li);
    }
  };

  fetch(`${ctx}/rest/dish/${dishId}`, {
    headers: { Accept: "application/json" },
  })
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((dish) => {
      category.textContent = dish.category || "";
      name.textContent = dish.name || "";
      description.textContent = dish.description || "";
      price.textContent =
        typeof dish.price === "number" ? money.format(dish.price) : "";

      if (dish.ingredients && dish.ingredients.length) {
        ingredientsSection.classList.remove("is-hidden");
        renderIngredients(dish.ingredients);
      }

      img.alt = `${dish.name || "Dish"} image`;
      img.classList.add("is-hidden");
      fetch(`${ctx}/rest/dish/${dishId}/image`, { headers: { Accept: "*/*" } })
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
    })
    .catch((error) => {
      // FIXME: show user-visible error message
      console.error("Failed to load dish", error);
    });
});
