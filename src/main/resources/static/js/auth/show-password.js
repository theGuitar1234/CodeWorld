let showPassword, passwords;

function cacheElements() {
  showPassword = document.getElementById("show-password");
  passwords = document.querySelectorAll("input[type='password']");
}

function showPasswordFunc() {
  passwords.forEach(p => p.setAttribute("type", "text"));
}

function hidePasswordFunc() {
  passwords.forEach(p => p.setAttribute("type", "password"));
}

function initShowPassword() {

  cacheElements();

  if (!showPassword || !passwords) return;

  showPassword.addEventListener("mousedown", showPasswordFunc);
  showPassword.addEventListener("mouseup", hidePasswordFunc);
}

export { initShowPassword };
