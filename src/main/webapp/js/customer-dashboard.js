document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.getElementById("customer-dashboard").dataset.ctx;

  const makeReviewBtn = (emoji, liked) => {
    const btn = document.createElement("button");
    btn.className = "review-btn";
    btn.textContent = emoji;
    btn.dataset.liked = String(liked);
    btn.setAttribute("aria-label", liked ? "Like this dish" : "Dislike this dish");
    return btn;
  };

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
