import { initExpand } from "./expand.js";
import { initScrollTop } from "./scroll-top.js";
import { initTable, populateTable } from "./table.js";
import { initTheme } from "./theme.js";
import { initDecide } from "./decide.js";
//import { initChart } from './chart.js';
import { initFragment } from "./fragment.js";
import { initMatchPassword } from "./match-password.js";
import { initShowPassword } from "./show-password.js";
import { initReport } from "./report.js";
import { initRemoveError } from "./removeError.js";
import { initFetchUser } from "./fetchUser.js";
import { initTransactionDialogs } from "./transactionDialogs.js";
import { initCommonDialog } from "./commongDialog.js";
import { initContactMain } from "./contact_main.js";
import { initRemoveSuccess } from "./removeSuccess.js";

function mountAdminPage() {
  initTable();
  initTransactionDialogs();

  populateTable();
  initDecide();
  //initChart();
  initMatchPassword();
  initShowPassword();
  initRemoveError();
  initRemoveSuccess();
  
  initReport();

  initFetchUser();

  initCommonDialog();
}

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initFragment();
    initScrollTop();
    initExpand();

    initMatchPassword();
    initShowPassword();

    initContactMain();
    
    mountAdminPage();

    document.addEventListener("spa:navigated", () => {
        mountAdminPage();
    });
});