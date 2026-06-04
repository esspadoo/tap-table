document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const form = document.getElementById("create-form");
  const alertContainer = document.getElementById("alert-container");
  const alertMessage = document.getElementById("alert-message");
  const imageInput = document.getElementById("image-input");
  const imagePreview = document.getElementById("image-preview");
  const dropzone = document.getElementById("image-dropzone");
  const dropzoneContent = dropzone.querySelector(".dropzone-content");
  const chipGroup = document.getElementById("ingredients-chip-group");
  const categorySelect = document.getElementById("category");
  const ingredientsSearch = document.getElementById("ingredients-search");

  loadCategories();
  loadIngredients();

  ingredientsSearch.addEventListener("input", () => {
    const q = ingredientsSearch.value.trim().toLowerCase();
    chipGroup.querySelectorAll(".chip").forEach((chip) => {
      const label = chip.querySelector("label");
      chip.style.display =
        !q || (label && label.textContent.toLowerCase().includes(q))
          ? ""
          : "none";
    });
  });

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

    const submitBtn = form.querySelector('[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = "Creating…";

    try {
      const fd = new FormData(form);
      if (!imageInput.files || imageInput.files.length === 0) fd.delete("image");

      const res = await fetch(ctx + "/rest/dish", {
        method: "POST",
        headers: { Accept: "application/json" },
        body: fd,
      });

      if (res.status === 201) {
        window.location.href = ctx + "/dashboard";
        return;
      }

      const data = await res.json().catch(() => ({}));
      showAlert(errMsg(data, "Failed to create dish."));
    } catch {
      showAlert("Network error. Please try again.");
    }

    submitBtn.disabled = false;
    submitBtn.textContent = "Create dish";
  });

  function loadCategories() {
    fetch(ctx + "/rest/category", { headers: { Accept: "application/json" } })
      .then((r) => {
        if (!r.ok) throw new Error("HTTP " + r.status);
        return r.json();
      })
      .then((payload) => {
        const categories = Array.isArray(payload["resource-list"])
          ? payload["resource-list"]
          : Array.isArray(payload)
            ? payload
            : [];
        categories.forEach((cat) => {
          const opt = document.createElement("option");
          opt.value = cat.name || cat;
          opt.textContent = cat.name || cat;
          categorySelect.appendChild(opt);
        });
      })
      .catch(() => {});
  }

  function loadIngredients() {
    fetch(ctx + "/rest/ingredient", { headers: { Accept: "application/json" } })
      .then((r) => {
        if (!r.ok) throw new Error("HTTP " + r.status);
        return r.json();
      })
      .then((payload) => {
        const ingredients = Array.isArray(payload["resource-list"])
          ? payload["resource-list"]
          : Array.isArray(payload)
            ? payload
            : [];
        ingredients.forEach((ing) => {
          const chip = document.createElement("div");
          chip.className = "chip";
          const cb = document.createElement("input");
          cb.type = "checkbox";
          cb.id = "ing-" + ing.id;
          cb.name = "ingredient_ids";
          cb.value = ing.id;
          const lbl = document.createElement("label");
          lbl.htmlFor = "ing-" + ing.id;
          lbl.textContent = ing.name;
          chip.appendChild(cb);
          chip.appendChild(lbl);
          chipGroup.appendChild(chip);
        });
      })
      .catch(() => {});
  }

  function showPreview(file) {
    if (!file) return;
    imagePreview.src = URL.createObjectURL(file);
    imagePreview.classList.add("visible");
    dropzoneContent.classList.add("hidden");
  }

  function errMsg(data, fallback) {
    const m = data?.message;
    return (typeof m === "string" ? m : m?.message) || fallback;
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
