import { initTable, populateTable} from "./table.js";
import { initStickyHeader } from "./sticky-header.js";
import { initScrollTop } from "./scroll-top.js";
import { initDialog } from "./dialog.js";
import { initShowPassword } from "./show-password.js";
import { initHero } from "./hero.js";
import { initTheme } from "./theme.js";

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initDialog();
    populateTable();
    initTable();
    initStickyHeader();
    initScrollTop();
    initHero();
    initShowPassword();
});