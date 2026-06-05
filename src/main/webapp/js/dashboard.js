document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.querySelector("main.dashboard").dataset.ctx;

  const makeReviewBtn = (emoji, liked) => {
    const btn = document.createElement("button");
    btn.className = "review-btn";
    btn.textContent = emoji;
    btn.dataset.liked = String(liked);
    btn.setAttribute("aria-label", liked ? "Like this dish" : "Dislike this dish");
    return btn;
  };

  document.querySelectorAll('[data-action="complete"]').forEach((btn) => {
    btn.addEventListener("click", async function () {
      const orderId = this.dataset.orderId;
      this.disabled = true;
      this.textContent = "Saving…";
      try {
        const res = await fetch(ctx + "/rest/order/" + orderId + "/COMPLETED", {
          method: "PUT",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
        });

        if (res.status === 200) {
          const tr = this.closest("tr");
          const statusCell = tr.querySelector("td:nth-child(5)");
          if (statusCell) {
            const badge = document.createElement("span");
            badge.className = "badge badge-completed";
            badge.textContent = "Completed";
            statusCell.replaceChildren(badge);
          }
          this.closest("td").replaceChildren();
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

  document.querySelectorAll("[data-needs-review='true']").forEach((span) => {
    const orderId = span.dataset.orderId;
    const dishId = span.dataset.dishId;

    const actions = document.createElement("span");
    actions.className = "review-actions";
    actions.appendChild(makeReviewBtn("👍", true));
    actions.appendChild(makeReviewBtn("👎", false));
    span.appendChild(actions);

    const [thumbUp, thumbDown] = actions.querySelectorAll(".review-btn");

    [thumbUp, thumbDown].forEach((btn) => {
      btn.addEventListener("click", async () => {
        const liked = btn.dataset.liked === "true";
        thumbUp.disabled = true;
        thumbDown.disabled = true;

        try {
          const res = await fetch(ctx + "/rest/order/" + orderId + "/review", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json",
            },
            body: JSON.stringify({
              dish_id: parseInt(dishId),
              is_liked: liked,
            }),
          });

          if (res.ok || res.status === 501) {
            span.className = liked
              ? "dish-eval dish-eval-liked"
              : "dish-eval dish-eval-disliked";
            span.textContent = liked ? "👍" : "👎";
            span.removeAttribute("data-needs-review");
          } else {
            thumbUp.disabled = false;
            thumbDown.disabled = false;
          }
        } catch {
          thumbUp.disabled = false;
          thumbDown.disabled = false;
        }
      });
    });
  });
});
