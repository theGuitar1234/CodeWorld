import { initTheme } from "./layout/theme.js";
import { initShowPassword } from "./auth/show-password.js"
import { initMatchPassword } from "./auth/match-password.js";
import { initIntercept } from "./auth/intercept.js";

document.addEventListener("DOMContentLoaded", () => {
    initMatchPassword();
    initTheme();
    initShowPassword();
    initIntercept();
});