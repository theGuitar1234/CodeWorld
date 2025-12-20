import { initTheme } from "./layout/theme.js";
import { initShowPassword } from "./auth/show-password.js"

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initShowPassword();
});