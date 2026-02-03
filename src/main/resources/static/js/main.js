import { initTable, populateTable} from "./table/table.js";
import { initStickyHeader } from "./layout/sticky-header.js";
import { initScrollTop } from "./layout/scroll-top.js";
import { initDialog } from "./table/dialog.js";
import { initTheme } from "./layout/theme.js";
import { initPagination } from "./table/pagination.js";
import { initReport } from "./dashboard/report.js";
import { initRemoveError } from "./error/removeError.js";
import { initProfilePicture } from "./account/profilePicture.js";
import { initI18n } from "./i18n/i18n.js";
import { initContactMain } from "./dashboard/contact_main.js";
import { initRemoveSuccess } from "./success/removeSuccess.js";
import { initCommonDialog } from "./account/commongDialog.js";


function mountDashboardPage() {
    initDialog();
    populateTable();
    initTable();
    initCommonDialog();
}

document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initPagination();
    initStickyHeader();
    initScrollTop();
    initReport();
    initRemoveError();
    initRemoveSuccess();
    initProfilePicture();
    initI18n();
    initContactMain();

    mountDashboardPage();

    document.addEventListener("pagination:navigated", () => {
        mountDashboardPage();
    });
});