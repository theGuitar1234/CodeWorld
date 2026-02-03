let showPassword, passwords;

function cacheElements() {
  showPassword = document.getElementById("show-password");
  passwords = document.querySelectorAll("input[type='password']");
}

function showPasswordFunc() {
  passwords.forEach((p) => (p.type = "text"));
}

function hidePasswordFunc() {
  passwords.forEach((p) => (p.type = "password"));
}

function isMobile() {
  const isTouch = window.matchMedia("(pointer: coarse)").matches;
  return isTouch;
}

function initShowPassword() {
  cacheElements();

  if (!showPassword || !passwords) return;

  if (isMobile) {
    showPassword.addEventListener("pointerdown", showPasswordFunc);
    showPassword.addEventListener("pointerup", hidePasswordFunc);
  } else {
    showPassword.addEventListener("mousedown", showPasswordFunc);
    showPassword.addEventListener("mouseup", hidePasswordFunc);
  }
}

export { initShowPassword };
