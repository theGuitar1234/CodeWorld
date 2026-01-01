import { initTheme } from "./layout/theme.js";
import { initShowPassword } from "./auth/show-password.js"
import { initMatchPassword } from "./auth/match-password.js";

document.addEventListener("DOMContentLoaded", () => {
    initMatchPassword();
    initTheme();
    initShowPassword();
});