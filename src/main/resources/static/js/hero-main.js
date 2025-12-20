import { initStickyHeader } from "./layout/sticky-header.js";
import { initScrollTop } from "./layout/scroll-top.js";
import { initTheme } from "./layout/theme.js";
import { initHero } from "./hero/hero.js"

document.addEventListener("DOMContentLoaded", () => {
    initHero();
    initTheme();
    initStickyHeader();
    initScrollTop();
});