document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const params = new URLSearchParams(location.search);
  const id = params.get("id");

  if (!id || isNaN(Number(id))) {
    window.location.href = ctx + "/dashboard";
    return;
  }

  const alertBanner = document.getElementById("alert-banner");
  const alertMessage = document.getElementById("alert-message");
  const img = document.getElementById("dish-image");
  const placeholder = document.getElementById("dish-image-placeholder");
  const nameEl = document.getElementById("dish-name");
  const categoryEl = document.getElementById("dish-category");
  const descriptionEl = document.getElementById("dish-description");
  const priceEl = document.getElementById("dish-price");
  const ingredientsSection = document.getElementById("dish-ingredients-section");
  const ingredientsList = document.getElementById("dish-ingredients-list");
  const editLink = document.getElementById("edit-link");
  const pageTitle = document.getElementById("page-title");

  editLink.href = ctx + "/dashboard/dishes/edit?id=" + id;

  fetch(ctx + "/rest/dish/" + id, { headers: { Accept: "application/json" } })
    .then((r) => {
      if (!r.ok) throw new Error("HTTP " + r.status);
      return r.json();
    })
    .then((dish) => {
      nameEl.textContent = dish.name || "";
      categoryEl.textContent = dish.category || "";
      descriptionEl.textContent = dish.description || "";
      priceEl.textContent =
        typeof dish.price === "number"
          ? new Intl.NumberFormat("it-IT", { style: "currency", currency: "EUR" }).format(dish.price)
          : "";

      if (pageTitle) pageTitle.textContent = dish.name || "Dish Detail";
      document.title = (dish.name || "Dish") + " - TapTable";

      if (dish.ingredients && dish.ingredients.length) {
        ingredientsSection.classList.remove("is-hidden");
        ingredientsList.replaceChildren();
        for (const ing of dish.ingredients) {
          const li = document.createElement("li");
          li.className = "ingredient-item";
          const nameSpan = document.createElement("span");
          nameSpan.className = "ingredient-name";
          nameSpan.textContent = ing.name || "Unknown ingredient";
          li.appendChild(nameSpan);
          ingredientsList.appendChild(li);
        }
      }

      img.alt = (dish.name || "Dish") + " image";
      img.classList.add("is-hidden");
      fetch(ctx + "/rest/dish/" + id + "/image", { headers: { Accept: "*/*" } })
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
    .catch(() => {
      showAlert("Failed to load dish.");
    });

  function showAlert(msg) {
    alertMessage.textContent = msg;
    alertBanner.classList.remove("is-hidden");
    alertBanner.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }
});
