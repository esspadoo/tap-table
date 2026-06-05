document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.body.dataset.ctx;
  const tbody = document.getElementById("ingredients-tbody");
  const countEl = document.getElementById("ingredients-count");
  const searchInput = document.getElementById("ingredients-search");
  const emptyState = document.getElementById("empty-state");
  const alertBanner = document.getElementById("alert-banner");
  const alertMessage = document.getElementById("alert-message");
  let allIngredients = [];

  fetch(ctx + "/rest/ingredient", { headers: { Accept: "application/json" } })
    .then((r) => {
      if (!r.ok) throw new Error("HTTP " + r.status);
      return r.json();
    })
    .then((payload) => {
      allIngredients = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      render(allIngredients);
    })
    .catch(() => {
      showAlert("Failed to load ingredients. Please refresh.");
    });

  searchInput.addEventListener("input", () => {
    const q = searchInput.value.trim().toLowerCase();
    const filtered = q
      ? allIngredients.filter((i) => (i.name || "").toLowerCase().includes(q))
      : allIngredients;
    render(filtered);
  });

  function render(ingredients) {
    tbody.replaceChildren();
    updateCount(ingredients.length);
    emptyState.hidden = ingredients.length > 0;

    const fragment = document.createDocumentFragment();
    ingredients.forEach((ingredient, i) => {
      const row = buildRow(ingredient);
      fragment.appendChild(row);
    });
    tbody.appendChild(fragment);
  }

  function buildRow(ingredient) {
    const tr = document.createElement("tr");
    tr.dataset.id = ingredient.id;

    const tdName = document.createElement("td");

    const nameInfo = document.createElement("div");
    nameInfo.className = "name-info";

    const nameText = document.createElement("span");
    nameText.className = "name-text";
    nameText.textContent = ingredient.name || "-";
    nameInfo.appendChild(nameText);

    if (ingredient.is_frozen) {
      const frozenEl = document.createElement("span");
      frozenEl.className = "frozen-indicator";
      frozenEl.textContent = "❄ Frozen";
      nameInfo.appendChild(frozenEl);
    }

    tdName.appendChild(nameInfo);

    const tdAllergens = document.createElement("td");

    if (ingredient.allergens && ingredient.allergens.length > 0) {
      const wrap = document.createElement("div");
      wrap.className = "allergen-tags";
      ingredient.allergens.forEach((a) => {
        const tag = document.createElement("span");
        tag.className = "allergen-tag";
        tag.textContent = a.replace(/_/g, " ");
        wrap.appendChild(tag);
      });
      tdAllergens.appendChild(wrap);
    } else {
      const dash = document.createElement("span");
      dash.className = "allergens-none";
      dash.textContent = "-";
      tdAllergens.appendChild(dash);
    }

    const tdActions = document.createElement("td");

    const editBtn = document.createElement("a");
    editBtn.href = ctx + "/dashboard/ingredients/edit?id=" + ingredient.id;
    editBtn.className = "btn btn-sm btn-outline";
    editBtn.textContent = "Edit";

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "btn btn-sm btn-danger";
    deleteBtn.textContent = "Delete";

    deleteBtn.addEventListener("click", () => {
      executeDelete(ingredient.id, tr, deleteBtn);
    });

    const actionsWrap = document.createElement("div");
    actionsWrap.className = "row-actions";
    actionsWrap.append(editBtn, deleteBtn);
    tdActions.append(actionsWrap);
    tr.append(tdName, tdAllergens, tdActions);
    return tr;
  }

  async function executeDelete(id, row, btn) {
    btn.disabled = true;
    btn.textContent = "Deleting…";

    try {
      const res = await fetch(ctx + "/rest/ingredient/" + id, {
        method: "DELETE",
        headers: { Accept: "application/json" },
      });

      if (res.ok) {
        row.remove();
        allIngredients = allIngredients.filter((i) => i.id !== id);
        const remaining = tbody.querySelectorAll("tr").length;
        updateCount(remaining);
        emptyState.hidden = remaining > 0;
      } else {
        const data = await res.json().catch(() => ({}));
        const m = data?.message;
        showAlert(
          (typeof m === "string" ? m : m?.message) ||
            "Failed to delete ingredient.",
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
    countEl.textContent = n + " ingredient" + (n !== 1 ? "s" : "");
  }

  function showAlert(msg) {
    alertMessage.textContent = msg;
    alertBanner.hidden = false;
    alertBanner.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }
});
