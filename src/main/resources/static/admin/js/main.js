import { initExpand } from "./expand.js";
import { initScrollTop } from "./scroll-top.js";
import { initDialog } from "./dialog.js";
import { initTable, populateTable } from "./table.js";
import { initTheme } from "./theme.js";
import { initDecide } from "./decide.js";
import { initChart } from './chart.js';
import { initFragment } from "./fragment.js";

function mountAdminPage() {
  initDialog();
  populateTable();
  initTable();
  initDecide();
  initChart();
}

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initFragment();
    initScrollTop();
    initExpand();

    mountAdminPage();

    document.addEventListener("spa:navigated", () => {
        mountAdminPage();
    });
});