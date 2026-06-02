document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector(".navbar")?.dataset.ctx || "";
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
        frozenEl.classList.remove("is-hidden");
        frozenEl.innerHTML = `<svg role="img" aria-label="frozen" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m10 20-1.25-2.5L6 18"/><path d="M10 4 8.75 6.5 6 6"/><path d="m14 20 1.25-2.5L18 18"/><path d="m14 4 1.25 2.5L18 6"/><path d="m17 21-3-6h-4"/><path d="m17 3-3 6 1.5 3"/><path d="M2 12h6.5L10 9"/><path d="m20 10-1.5 2 1.5 2"/><path d="M22 12h-6.5L14 15"/><path d="m4 10 1.5 2L4 14"/><path d="m7 21 3-6-1.5-3"/><path d="m7 3 3 6h4"/></svg> Frozen ingredient`;
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
      img.classList.add("is-hidden");
      fetch(`${ctx}/rest/ingredient/${ingredientId}/image`, { headers: { Accept: "*/*" } })
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
      console.error("Failed to load ingredient", error);
    });
});
