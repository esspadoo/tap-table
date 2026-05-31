const navbar = document.querySelector(".navbar");
const burger = document.querySelector(".burger-button");
const ctx = navbar.dataset.ctx || "";

const link = (href, text) => {
  const a = document.createElement("a");
  a.href = ctx + href;
  a.textContent = text;
  return a;
};

burger.addEventListener("click", () => {
  const expanded = burger.getAttribute("aria-expanded") === "true";
  burger.setAttribute("aria-expanded", String(!expanded));
  navbar.classList.toggle("close");
});

fetch(ctx + "/rest/user")
  .then((res) => {
    if (!res.ok) return;
    return res.json();
  })
  .then((user) => {
    if (!user) return;

    const items = navbar.querySelector(".items");
    const actions = navbar.querySelector(".actions");

    items.replaceChildren();
    actions.replaceChildren();

    items.appendChild(link("/", "Home"));
    items.appendChild(link("/ingredients", "Ingredients"));

    const dashboardLink = link("/dashboard", "Dashboard");
    dashboardLink.className = "btn btn-primary";
    actions.appendChild(dashboardLink);

    const form = document.createElement("form");
    form.method = "post";
    form.action = ctx + "/logout";
    const signOut = document.createElement("button");
    signOut.type = "submit";
    signOut.className = "btn btn-outline";
    signOut.textContent = "Sign out";
    form.appendChild(signOut);
    actions.appendChild(form);
  });
