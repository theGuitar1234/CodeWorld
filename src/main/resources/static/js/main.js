import { initTable, populateTable} from "./table/table.js";
import { initStickyHeader } from "./layout/sticky-header.js";
import { initScrollTop } from "./layout/scroll-top.js";
import { initDialog } from "./table/dialog.js";
import { initTheme } from "./layout/theme.js";
import { initPagination } from "./table/pagination.js";

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initDialog();
    populateTable();
    initTable();
    initPagination();
    initStickyHeader();
    initScrollTop();
});