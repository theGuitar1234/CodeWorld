let showPassword, password;

function cacheElements() {
  showPassword = document.getElementById("show-password");
  password = document.getElementById("password");
}

function showPasswordFunc() {
    password.setAttribute("type", "text");
}

function hidePasswordFunc() {
    password.setAttribute("type", "password");
}

function initShowPassword() {

  cacheElements();

  if (!showPassword || !password) return;

  showPassword.addEventListener("mousedown", showPasswordFunc);
  showPassword.addEventListener("mouseup", hidePasswordFunc);
}

export { initShowPassword };
