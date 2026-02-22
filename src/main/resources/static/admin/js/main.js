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
import { initExpandables } from "./expandables.js";
import { initI18n } from "./i18n.js";

function mountAdminPage() {
  initTable();
  initTransactionDialogs();

  populateTable();
  initDecide();
  //initChart();
  initMatchPassword();
  initShowPassword();
  
  initReport();

  initFetchUser();

  initCommonDialog();
}

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initFragment();
    initScrollTop();

    initI18n();
    
    initExpand();

    initRemoveError();
    initRemoveSuccess();

    initExpandables();

    initMatchPassword();
    initShowPassword();

    initContactMain();
    
    mountAdminPage();

    document.addEventListener("spa:navigated", () => {
        mountAdminPage();
    });
});