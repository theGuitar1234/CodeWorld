let 
  form,
  p1,
  p2;

function cacheElements() {
  form = document.querySelector("form");
  p1 = document.getElementById("password");
  p2 = document.getElementById("password2");
}

function validatePasswords() {
  p1.setCustomValidity("");

  if (p1.value && p1.value !== p2.value) {
    p1.setCustomValidity("Passwords must match");
  }
}

function initMatchPassword() {

  cacheElements();

  if (!form || !p1 || !p2) return;

  p1.addEventListener("input", validatePasswords);
  p2.addEventListener("input", validatePasswords);

  form.addEventListener("submit", (e) => {
    validatePasswords();

    if (!form.checkValidity()) {
      p1.reportValidity();
      e.preventDefault();
    }
  });
}

export { initMatchPassword };
