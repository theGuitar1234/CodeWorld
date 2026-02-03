let isBound = false;
let select, redirectInput;

function cacheElements() {
  select = document.getElementById("lang");
  redirectInput = document.getElementById("redirectUrl");
}

function updateFormAction() {
  redirectInput.value = window.location.pathname + window.location.search;
  select.form.submit();
}

function initI18n() {
  if (isBound) return;
  isBound = true;

  cacheElements();
  if (!select || !redirectInput) return;

  redirectInput.value = window.location.pathname + window.location.search;

  select.addEventListener("change", updateFormAction);
}

export { initI18n };
