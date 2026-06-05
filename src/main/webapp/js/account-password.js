document.addEventListener("DOMContentLoaded", () => {
  const newPasswordInput = document.getElementById("new_password");
  const ruleLength = document.getElementById("rule-length");

  newPasswordInput.addEventListener("input", () => {
    ruleLength.classList.toggle("met", newPasswordInput.value.length >= 8);
  });
});
