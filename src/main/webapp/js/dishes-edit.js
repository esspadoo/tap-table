document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const params = new URLSearchParams(location.search);
  const id = params.get("id");

  if (!id || isNaN(Number(id))) {
    window.location.href = ctx + "/dashboard";
    return;
  }

  const form = document.getElementById("edit-form");
  const alertContainer = document.getElementById("alert-container");
  const alertMessage = document.getElementById("alert-message");
  const imageInput = document.getElementById("image-input");
  const imagePreview = document.getElementById("image-preview");
  const dropzone = document.getElementById("image-dropzone");
  const dropzoneContent = document.getElementById("dropzone-content");
  const pageTitle = document.getElementById("page-title");
  const chipGroup = document.getElementById("ingredients-chip-group");
  const categorySelect = document.getElementById("category");
  const ingredientsSearch = document.getElementById("ingredients-search");

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

  loadCategories().then(() => loadIngredients().then(() => loadDish()));

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
    submitBtn.textContent = "Saving…";

    try {
      const fd = new FormData(form);
      fd.append("id", id);
      if (!imageInput.files || imageInput.files.length === 0) fd.delete("image");

      const res = await fetch(ctx + "/rest/dish", {
        method: "PUT",
        headers: { Accept: "application/json" },
        body: fd,
      });

      if (res.ok) {
        window.location.href = ctx + "/dashboard";
        return;
      }

      const data = await res.json().catch(() => ({}));
      showAlert(errMsg(data, "Failed to save changes."));
    } catch {
      showAlert("Network error. Please try again.");
    }

    submitBtn.disabled = false;
    submitBtn.textContent = "Save changes";
  });

  function loadCategories() {
    return fetch(ctx + "/rest/category", { headers: { Accept: "application/json" } })
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
    return fetch(ctx + "/rest/ingredient", { headers: { Accept: "application/json" } })
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

  async function loadDish() {
    try {
      const res = await fetch(ctx + "/rest/dish/" + id, {
        headers: { Accept: "application/json" },
      });

      if (res.status === 404) {
        showAlert("Dish not found.");
        return;
      }

      if (!res.ok) {
        showAlert("Failed to load dish.");
        return;
      }

      const data = await res.json();
      populate(data);
      loadExistingImage();
    } catch {
      showAlert("Network error loading dish.");
    }
  }

  function populate(data) {
    document.getElementById("name").value = data.name || "";
    document.getElementById("description").value = data.description || "";
    document.getElementById("price").value = data.price != null ? data.price : "";

    if (data.category) {
      const opt = categorySelect.querySelector('option[value="' + data.category + '"]');
      if (opt) opt.selected = true;
    }

    if (pageTitle) pageTitle.textContent = "Edit: " + data.name;
    document.title = "Edit " + data.name + " - TapTable";

    if (Array.isArray(data.ingredients)) {
      data.ingredients.forEach((ing) => {
        const cb = document.querySelector(
          'input[name="ingredient_ids"][value="' + ing.id + '"]',
        );
        if (cb) cb.checked = true;
      });
    }
  }

  function loadExistingImage() {
    fetch(ctx + "/rest/dish/" + id + "/image", { headers: { Accept: "*/*" } })
      .then((r) => (r.ok ? r.blob() : Promise.reject()))
      .then((blob) => {
        imagePreview.src = URL.createObjectURL(blob);
        imagePreview.classList.add("visible");
        dropzoneContent.classList.add("hidden");
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
