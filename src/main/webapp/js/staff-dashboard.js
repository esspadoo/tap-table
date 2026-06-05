document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.getElementById("staff-dashboard").dataset.ctx;

  document.querySelectorAll('[data-action="complete"]').forEach((btn) => {
    btn.addEventListener("click", async function () {
      const orderId = this.dataset.orderId;
      this.disabled = true;
      this.textContent = "Saving…";
      try {
        const res = await fetch(ctx + "/rest/order/" + orderId + "/COMPLETED", {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
        });

        if (res.status === 200) {
          location.reload();
          return;
        }

        this.disabled = false;
        this.textContent = "Mark complete";

        if (res.status === 401) {
          alert("Unauthorized.");
        } else if (res.status === 403) {
          alert("You do not have permission to modify this order.");
        } else if (res.status === 400) {
          let msg = "Invalid request.";
          try { const body = await res.json(); if (body.message) msg = body.message; } catch {}
          alert(msg);
        } else {
          alert("Unexpected server error.");
        }
      } catch {
        this.disabled = false;
        this.textContent = "Mark complete";
        alert("Unexpected server error.");
      }
    });
  });
});
