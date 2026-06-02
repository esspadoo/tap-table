document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const form = document.getElementById("create-form");
  const alertContainer = document.getElementById("alert-container");
  const alertMessage = document.getElementById("alert-message");
  const imageInput = document.getElementById("image-input");
  const imagePreview = document.getElementById("image-preview");
  const dropzone = document.getElementById("image-dropzone");
  const dropzoneContent = dropzone.querySelector(".dropzone-content");
  const ingredientsGroup = document.getElementById("ingredients-group");
  const categorySelect = document.getElementById("category");

  fetch(ctx + "/rest/ingredient", { headers: { Accept: "application/json" } })
    .then((response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return response.json();
    })
    .then((payload) => {
      const ingredients = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      renderIngredients(ingredients);
    })
    .catch((error) => {
      console.error("Failed to load ingredients", error);
    });

  const renderIngredients = (ingredients) => {
    if (!ingredients.length) {
      ingredientsGroup.innerHTML =
        '<p class="section-hint">No ingredients available.</p>';
      return;
    }

    for (const ingredient of ingredients) {
      const chip = document.createElement("div");
      chip.className = "chip";

      const input = document.createElement("input");
      input.type = "checkbox";
      input.id = `ing-${ingredient.id}`;
      input.name = "ingredient_ids";
      input.value = ingredient.id;

      const label = document.createElement("label");
      label.htmlFor = `ing-${ingredient.id}`;
      label.textContent = ingredient.name || "Unnamed";

      chip.append(input, label);
      ingredientsGroup.appendChild(chip);
    }
  };

  imageInput.addEventListener("change", () => showPreview(imageInput.files[0]));

  dropzone.addEventListener("dragover", (e) => {
    e.preventDefault();
    dropzone.classList.add("drag-over");
  });

  dropzone.addEventListener("dragleave", () =>
    dropzone.classList.remove("drag-over"),
  );

  dropzone.addEventListener("drop", (e) => {
    e.preventDefault();
    dropzone.classList.remove("drag-over");
    const file = e.dataTransfer.files[0];
    if (!file) return;
    const dt = new DataTransfer();
    dt.items.add(file);
    imageInput.files = dt.files;
    showPreview(file);
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    hideAlert();

    const nameInput = document.getElementById("name");
    const priceInput = document.getElementById("price");

    if (!nameInput.value.trim()) {
      showAlert("Name is required.");
      nameInput.focus();
      return;
    }

    if (!priceInput.value.trim()) {
      showAlert("Price is required.");
      priceInput.focus();
      return;
    }

    const priceVal = parseFloat(priceInput.value);
    if (isNaN(priceVal) || priceVal <= 0) {
      showAlert("Price must be a positive number.");
      priceInput.focus();
      return;
    }

    const selectedIngredients = form.querySelectorAll(
      'input[name="ingredient_ids"]:checked',
    );
    if (!selectedIngredients.length) {
      showAlert("At least one ingredient is required.");
      return;
    }

    const categoryVal = categorySelect.value;
    if (!categoryVal) {
      showAlert("Category is required.");
      categorySelect.focus();
      return;
    }

    const submitBtn = form.querySelector('[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = "Creating\u2026";

    try {
      const res = await fetch(ctx + "/rest/dish", {
        method: "POST",
        headers: { Accept: "application/json" },
        body: new FormData(form),
      });

      if (res.status === 201) {
        window.location.href = ctx + "/dashboard/dish";
        return;
      }

      const data = await res.json().catch(() => ({}));
      showAlert(data.message || "Failed to create dish.");
    } catch {
      showAlert("Network error. Please try again.");
    }

    submitBtn.disabled = false;
    submitBtn.textContent = "Create dish";
  });

  function showPreview(file) {
    if (!file) return;
    imagePreview.src = URL.createObjectURL(file);
    imagePreview.classList.add("visible");
    dropzoneContent.classList.add("hidden");
  }

  function showAlert(msg) {
    alertMessage.textContent = msg;
    alertContainer.classList.add("visible");
    alertContainer.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

  function hideAlert() {
    alertContainer.classList.remove("visible");
  }
});