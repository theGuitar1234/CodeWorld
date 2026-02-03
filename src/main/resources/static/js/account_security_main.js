import { initStickyHeader } from "./layout/sticky-header.js";
import { initScrollTop } from "./layout/scroll-top.js";
import { initTheme } from "./layout/theme.js";
import { initFragment } from "./account/fragment.js";
import { initMatchPassword } from "./auth/match-password.js";
import { initShowPassword } from "./auth/show-password.js";
import { initProfilePicture } from "./account/profilePicture.js";
import { initI18n } from "./i18n/i18n.js";
import { initContactMain } from "./dashboard/contact_main.js";
import { initCommonDialog } from "./account/commongDialog.js";

document.addEventListener("DOMContentLoaded", () => {
    initShowPassword();
    initMatchPassword();
    initTheme();
    initFragment();
    initStickyHeader();
    initScrollTop();
    initProfilePicture();
    initI18n();
    initContactMain();
    initCommonDialog();
});