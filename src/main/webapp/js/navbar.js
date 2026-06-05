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

    const signOut = document.createElement("button");
    signOut.className = "btn btn-outline";
    signOut.textContent = "Sign out";
    signOut.addEventListener("click", () => {
      signOut.disabled = true;
      fetch(ctx + "/logout", { method: "POST" })
        .then((res) => {
          if (res.ok) {
            location.href = ctx + "/";
          } else {
            signOut.disabled = false;
            signOut.textContent = "Sign out failed — try again";
          }
        })
        .catch(() => {
          signOut.disabled = false;
          signOut.textContent = "Sign out failed — try again";
        });
    });
    actions.appendChild(signOut);
  });
