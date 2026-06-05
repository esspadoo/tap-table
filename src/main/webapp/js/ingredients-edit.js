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

  loadIngredient();

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

      const res = await fetch(ctx + "/rest/ingredient", {
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

  async function loadIngredient() {
    try {
      const res = await fetch(ctx + "/rest/ingredient/" + id, {
        headers: { Accept: "application/json" },
      });

      if (res.status === 404) {
        showAlert("Ingredient not found.");
        return;
      }

      if (!res.ok) {
        showAlert("Failed to load ingredient.");
        return;
      }

      const data = await res.json();
      populate(data);
      loadExistingImage();
    } catch {
      showAlert("Network error loading ingredient.");
    }
  }

  function populate(data) {
    document.getElementById("name").value = data.name || "";
    document.getElementById("is_frozen").checked = !!data.is_frozen;

    if (pageTitle) pageTitle.textContent = "Edit: " + data.name;
    document.title = "Edit " + data.name + " - TapTable";

    if (Array.isArray(data.allergens)) {
      data.allergens.forEach((a) => {
        const cb = document.querySelector(
          'input[name="allergens"][value="' + a + '"]',
        );
        if (cb) cb.checked = true;
      });
    }
  }

  function loadExistingImage() {
    fetch(ctx + "/rest/ingredient/" + id + "/image", { headers: { Accept: "*/*" } })
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
    alertContainer.hidden = false;
    alertContainer.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

  function hideAlert() {
    alertContainer.hidden = true;
  }
});
