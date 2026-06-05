document.addEventListener("DOMContentLoaded", () => {
  const ctx = document.getElementById("promotions-manage-page").dataset.ctx;
  const tbody = document.getElementById("promotions-tbody");
  const tableWrap = tbody.closest(".data-table-wrap");
  const countEl = document.getElementById("promotions-count");
  const searchInput = document.getElementById("promotions-search");
  const emptyState = document.getElementById("empty-state");
  const alertBanner = document.getElementById("alert-banner");
  const alertMessage = document.getElementById("alert-message");
  let allPromotions = [];

  fetch(ctx + "/rest/promotion", { headers: { Accept: "application/json" } })
    .then((r) => {
      if (!r.ok) throw new Error("HTTP " + r.status);
      return r.json();
    })
    .then((payload) => {
      allPromotions = Array.isArray(payload["resource-list"])
        ? payload["resource-list"]
        : Array.isArray(payload)
          ? payload
          : [];
      render(allPromotions);
    })
    .catch(() => {
      showAlert("Failed to load promotions. Please refresh.");
    });

  searchInput.addEventListener("input", () => {
    const q = searchInput.value.trim().toLowerCase();
    const filtered = q
      ? allPromotions.filter(
          (p) =>
            (p.code || "").toLowerCase().includes(q) ||
            (p.description || "").toLowerCase().includes(q),
        )
      : allPromotions;
    render(filtered);
  });

  function render(promotions) {
    tbody.replaceChildren();
    updateCount(promotions.length);
    const isEmpty = promotions.length === 0;
    tableWrap.hidden = isEmpty;
    emptyState.hidden = !isEmpty;

    const fragment = document.createDocumentFragment();
    promotions.forEach((p) => fragment.appendChild(buildRow(p)));
    tbody.appendChild(fragment);
  }

  function buildRow(p) {
    const tr = document.createElement("tr");

    const tdCode = document.createElement("td");
    tdCode.textContent = p.code || "-";

    const tdDiscount = document.createElement("td");
    tdDiscount.textContent = p.discount != null ? p.discount + "%" : "-";

    const tdDesc = document.createElement("td");
    tdDesc.textContent = p.description || "-";

    const tdFrom = document.createElement("td");
    tdFrom.textContent = fmtDate(p.valid_from);

    const tdTo = document.createElement("td");
    tdTo.textContent = fmtDate(p.valid_to);

    tr.append(tdCode, tdDiscount, tdDesc, tdFrom, tdTo);
    return tr;
  }

  function updateCount(n) {
    countEl.textContent = n + " promotion" + (n !== 1 ? "s" : "");
  }

  function fmtDate(iso) {
    if (!iso) return "-";
    return iso.replace("T", " ").slice(0, 16);
  }

  function showAlert(msg) {
    alertMessage.textContent = msg;
    alertBanner.hidden = false;
    alertBanner.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }
});
