document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector("main.dashboard").dataset.ctx;

  document.querySelectorAll('[data-action="complete"]').forEach((btn) => {
    btn.addEventListener("click", async function () {
      const orderId = this.dataset.orderId;
      this.disabled = true;
      this.textContent = "Saving...";
      try {
        const res = await fetch(ctx + "/rest/order/" + orderId + "/COMPLETED", {
          method: "PUT",
        });
        if (res.ok) {
          location.reload();
        } else {
          this.disabled = false;
          this.textContent = "Mark complete";
        }
      } catch {
        this.disabled = false;
        this.textContent = "Mark complete";
      }
    });
  });

  document.querySelectorAll('[data-action="cancel"]').forEach((btn) => {
    btn.addEventListener("click", async function () {
      const orderId = this.dataset.orderId;
      this.disabled = true;
      this.textContent = "Cancelling...";
      try {
        const res = await fetch(ctx + "/rest/order/" + orderId + "/", {
          method: "DELETE",
        });
        if (res.ok) {
          location.reload();
        } else {
          this.disabled = false;
          this.textContent = "Cancel";
        }
      } catch {
        this.disabled = false;
        this.textContent = "Cancel";
      }
    });
  });
});
