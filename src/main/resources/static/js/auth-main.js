import { initTheme } from "./layout/theme.js";
import { initShowPassword } from "./auth/show-password.js"
import { initMatchPassword } from "./auth/match-password.js";
import { initIntercept } from "./auth/intercept.js";
import { initRemoveError } from "./error/removeError.js";
import { initI18n } from "./i18n/i18n.js";
import { initRemoveSuccess } from "./success/removeSuccess.js";

document.addEventListener("DOMContentLoaded", () => {
    initMatchPassword();
    initTheme();
    initShowPassword();
    initIntercept();
    initRemoveError();
    initRemoveSuccess();
    initI18n();
});