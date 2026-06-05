document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const form = document.getElementById("create-form");
  const alertContainer = document.getElementById("alert-container");
  const alertMessage = document.getElementById("alert-message");
  const imageInput = document.getElementById("image-input");
  const imagePreview = document.getElementById("image-preview");
  const dropzone = document.getElementById("image-dropzone");
  const dropzoneContent = document.getElementById("dropzone-content");

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

      const res = await fetch(ctx + "/rest/ingredient", {
        method: "POST",
        headers: { Accept: "application/json" },
        body: fd,
      });

      if (res.status === 201) {
        window.location.href = ctx + "/dashboard";
        return;
      }

      const data = await res.json().catch(() => ({}));
      showAlert(errMsg(data, "Failed to create ingredient."));
    } catch {
      showAlert("Network error. Please try again.");
    }

    submitBtn.disabled = false;
    submitBtn.textContent = "Create ingredient";
  });

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
