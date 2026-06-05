document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.getElementById("ingredient-detail-page").dataset.ctx;
  const img = document.getElementById("ingredient-image");
  const placeholder = document.getElementById("ingredient-image-placeholder");
  const name = document.getElementById("ingredient-name");
  const frozenEl = document.getElementById("ingredient-frozen");
  const allergensSection = document.getElementById("allergens-section");
  const allergensList = document.getElementById("allergens-list");

  const params = new URLSearchParams(window.location.search);
  const ingredientId = params.get("id");

  if (!ingredientId) return;

  fetch(`${ctx}/rest/ingredient/${ingredientId}`, {
    headers: { Accept: "application/json" },
  })
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((ingredient) => {
      name.textContent = ingredient.name || "";

      if (ingredient.is_frozen === true) {
        frozenEl.hidden = false;
      }

      if (ingredient.allergens && ingredient.allergens.length) {
        allergensSection.hidden = false;
        allergensList.replaceChildren();
        for (const a of ingredient.allergens) {
          const li = document.createElement("li");
          li.className = "allergen-badge";
          li.textContent = a;
          allergensList.appendChild(li);
        }
      }

      img.alt = `${ingredient.name || "Ingredient"} image`;
      img.hidden = true;
      fetch(`${ctx}/rest/ingredient/${ingredientId}/image`, {
        headers: { Accept: "*/*" },
      })
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
      console.error("Failed to load ingredient", error);
    });
});
