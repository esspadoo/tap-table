document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.getElementById("dish-detail-page").dataset.ctx;
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

  ingredientsSection.hidden = true;

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
        frozenSpan.textContent = "\u2744";
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
        ingredientsSection.hidden = false;
        renderIngredients(dish.ingredients);
      }

      img.alt = `${dish.name || "Dish"} image`;
      img.hidden = true;
      fetch(`${ctx}/rest/dish/${dishId}/image`, { headers: { Accept: "*/*" } })
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
    })
    .catch((error) => {
      console.error("Failed to load dish", error);
    });
});
