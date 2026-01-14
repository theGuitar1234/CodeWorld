import { initStickyHeader } from "./layout/sticky-header.js";
import { initScrollTop } from "./layout/scroll-top.js";
import { initTheme } from "./layout/theme.js";
import { initFragment } from "./account/fragment.js";

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initFragment();
    initStickyHeader();
    initScrollTop();
});