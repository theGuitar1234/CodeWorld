import { initStickyHeader } from "./layout/sticky-header.js";
import { initScrollTop } from "./layout/scroll-top.js";
import { initTheme } from "./layout/theme.js";
import { initHero } from "./hero/hero.js"
import { initI18n } from "./i18n/i18n.js";

document.addEventListener("DOMContentLoaded", () => {
    initHero();
    initTheme();
    initStickyHeader();
    initScrollTop();
    initI18n();
});