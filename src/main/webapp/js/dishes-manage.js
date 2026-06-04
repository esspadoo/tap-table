document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const tbody = document.getElementById("dishes-tbody");
  const countEl = document.getElementById("dishes-count");
  const searchInput = document.getElementById("dishes-search");
  const emptyState = document.getElementById("empty-state");
  const alertBanner = document.getElementById("alert-banner");
  const alertMessage = document.getElementById("alert-message");
  let allDishes = [];

  fetch(ctx + "/rest/dish", { headers: { Accept: "application/json" } })
    .then((r) => {
      if (!r.ok) throw new Error("HTTP " + r.status);
      return r.json();
    })
    .then((payload) => {
      allDishes = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      render(allDishes);
    })
    .catch(() => {
      showAlert("Failed to load dishes. Please refresh.");
    });

  searchInput.addEventListener("input", () => {
    const q = searchInput.value.trim().toLowerCase();
    const filtered = q
      ? allDishes.filter((d) => (d.name || "").toLowerCase().includes(q))
      : allDishes;
    render(filtered);
  });

  function render(dishes) {
    tbody.replaceChildren();
    updateCount(dishes.length);
    emptyState.classList.toggle("is-hidden", dishes.length > 0);

    const fragment = document.createDocumentFragment();
    dishes.forEach((dish) => {
      const row = buildRow(dish);
      fragment.appendChild(row);
    });
    tbody.appendChild(fragment);
  }

  function buildRow(dish) {
    const tr = document.createElement("tr");
    tr.dataset.id = dish.id;

    // ── Name ──
    const tdName = document.createElement("td");

    const nameInfo = document.createElement("div");
    nameInfo.className = "name-info";

    const nameText = document.createElement("span");
    nameText.className = "name-text";
    nameText.textContent = dish.name || "-";
    nameInfo.appendChild(nameText);

    tdName.appendChild(nameInfo);

    const tdCategory = document.createElement("td");
    tdCategory.textContent = dish.category || "-";

    const tdActions = document.createElement("td");

    const editBtn = document.createElement("a");
    editBtn.href = ctx + "/dashboard/dishes/edit?id=" + dish.id;
    editBtn.className = "btn btn-sm btn-outline";
    editBtn.textContent = "Edit";

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "btn btn-sm btn-danger";
    deleteBtn.textContent = "Delete";

    deleteBtn.addEventListener("click", () => {
      executeDelete(dish.id, tr, deleteBtn);
    });

    const actionsWrap = document.createElement("div");
    actionsWrap.className = "row-actions";
    actionsWrap.append(editBtn, deleteBtn);
    tdActions.append(actionsWrap);
    tr.append(tdName, tdCategory, tdActions);
    return tr;
  }

  async function executeDelete(id, row, btn) {
    btn.disabled = true;
    btn.textContent = "Deleting…";

    try {
      const res = await fetch(ctx + "/rest/dish/" + id, {
        method: "DELETE",
        headers: { Accept: "application/json" },
      });

      if (res.ok) {
        row.remove();
        allDishes = allDishes.filter((d) => d.id !== id);
        const remaining = tbody.querySelectorAll("tr").length;
        updateCount(remaining);
        emptyState.classList.toggle("is-hidden", remaining > 0);
      } else {
        const data = await res.json().catch(() => ({}));
        const m = data?.message;
        showAlert(
          (typeof m === "string" ? m : m?.message) ||
            "Failed to delete dish.",
        );
        btn.disabled = false;
        btn.textContent = "Delete";
      }
    } catch {
      showAlert("Network error. Please try again.");
      btn.disabled = false;
      btn.textContent = "Delete";
    }
  }

  function updateCount(n) {
    countEl.textContent = n + " dish" + (n !== 1 ? "es" : "");
  }

  function showAlert(msg) {
    alertMessage.textContent = msg;
    alertBanner.classList.remove("is-hidden");
    alertBanner.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }
});
