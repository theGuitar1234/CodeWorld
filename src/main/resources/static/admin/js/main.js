import { initExpand } from "./expand.js";
import { initScrollTop } from "./scroll-top.js";
import { initDialog } from "./dialog.js";
import { initTable, populateTable } from "./table.js";
import { initTheme } from "./theme.js";
import { initDecide } from "./decide.js";

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initDialog();
    populateTable();
    initTable();
    initDecide();
    initScrollTop();
    initExpand();
});