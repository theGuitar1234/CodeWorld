import { initTable, populateTable} from "./table.js";
import { initStickyHeader } from "./sticky-header.js";
import { initScrollTop } from "./scroll-top.js";
import { initDialog } from "./dialog.js";

document.addEventListener("DOMContentLoaded", () => {
    initDialog();
    populateTable();
    initTable();
    initStickyHeader();
    initScrollTop();
});