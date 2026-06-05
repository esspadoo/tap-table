document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.getElementById("promotion-create-page").dataset.ctx;
  const form = document.getElementById("create-form");
  const alertContainer = document.getElementById("alert-container");
  const alertMessage = document.getElementById("alert-message");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    hideAlert();

    const submitBtn = form.querySelector('[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = "Creating…";

    try {
      const validFrom = document.getElementById("valid_from").value;
      const validTo = document.getElementById("valid_to").value;

      const body = {
        code: document.getElementById("code").value.trim(),
        discount: parseFloat(document.getElementById("discount").value),
        description: document.getElementById("description").value.trim() || null,
        valid_from: validFrom ? validFrom + ":00" : null,
        valid_to: validTo ? validTo + ":00" : null,
      };

      const res = await fetch(ctx + "/rest/promotion", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify(body),
      });

      if (res.status === 201) {
        window.location.href = ctx + "/dashboard/promotions";
        return;
      }

      const data = await res.json().catch(() => ({}));
      showAlert(errMsg(data, "Failed to create promotion."));
    } catch {
      showAlert("Network error. Please try again.");
    }

    submitBtn.disabled = false;
    submitBtn.textContent = "Create promotion";
  });

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
