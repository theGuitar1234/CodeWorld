import { showDialog } from "./dialog.js";

const opt1 = "checked",
  opt2 = "rejected",
  opt3 = "pending";

let 
  dialogStatusIcon,
  dialogStatusIcons,
  tableBody,
  tables,
  dataTotal,
  dataTotalHeader,
  dataPaidBy,
  dataTransactionId,
  dataDetails,
  dataStatus,
  dataFee,
  dataAmount,
  dataDate,
  dataPaidByHeader,
  rows,
  row,
  statusCell,
  status,
  dialog;

function cacheElements() {
  tables = document.querySelectorAll("[dialog-table]");

  dialogStatusIcons = document.querySelectorAll("[dialog-status-icon]");

  //tableBody = document.querySelector("tbody");
}

function resetDialogStatusClasses() {
  if (!dialogStatusIcons) return;

  dialogStatusIcons.forEach(dialogStatusIcon => dialogStatusIcon.classList.remove(opt1, opt2, opt3));
}

function populateDialog(row) {
  if (!row) return;

  resetDialogStatusClasses();

  dialog = row.parentNode.parentNode.parentNode.firstElementChild;

  dataPaidByHeader = dialog.querySelector("[data-paid-by-header]");
  dataTotalHeader = dialog.querySelector("[data-total-header]");
  dataDate = dialog.querySelector("[data-date]");
  dataAmount = dialog.querySelector("[data-amount]");
  dataFee = dialog.querySelector("[data-fee]");
  dataTotal = dialog.querySelector("[data-total]");
  dataPaidBy = dialog.querySelector("[data-paid-by]");
  dataTransactionId = dialog.querySelector("[data-transaction-id]");
  dataDetails = dialog.querySelector("[data-details]");
  dataStatus = dialog.querySelector("[data-status]");

  dataTotal.textContent = row.dataset.total;
  dataTotalHeader.textContent = row.dataset.total;
  dataPaidByHeader.textContent = row.dataset.paidBy;
  dataPaidBy.textContent = row.dataset.paidBy;
  dataDate.textContent = row.dataset.date;
  dataAmount.textContent = row.dataset.amount;
  dataFee.textContent = row.dataset.fee;
  dataTransactionId.textContent = row.dataset.transactionId;
  dataDetails.textContent = row.dataset.details;
  dataStatus.textContent = row.dataset.status;

  status = row.dataset.status?.toLowerCase();
  dialogStatusIcon = dialog.querySelector("[dialog-status-icon");

  switch (status) {
    case opt1:
      dialogStatusIcon.classList.add(opt1);
      break;
    case opt2:
      dialogStatusIcon.classList.add(opt2);
      break;
    case opt3:
      dialogStatusIcon.classList.add(opt3);
      break;
  }
}

function populateTable() {
  if (!tables) cacheElements();
  if (!tables) return;

  tables.forEach((table) => {
    rows = table.querySelectorAll("tr");

    rows.forEach((r) => {
      statusCell = r.querySelector("[status-icon]");

      if (!statusCell || !statusCell.firstElementChild) return;

      status = r.dataset.status?.toLowerCase();
      switch (status) {
        case opt1:
          statusCell.firstElementChild.classList.add(opt1);
          break;
        case opt2:
          statusCell.firstElementChild.classList.add(opt2);
          break;
        case opt3:
          statusCell.firstElementChild.classList.add(opt3);
          break;
      }
    });
  });
}

function initTable() {
  cacheElements();

  if (!tables) return;

  tables.forEach((table) => {
    tableBody = table.querySelector("tbody");
    tableBody.addEventListener("click", (e) => {

      row = e.target.closest("tr");

      if (!row) return;

      try {
        populateDialog(row);
        dialog = row.parentNode.parentNode.parentNode.firstElementChild;
        showDialog(dialog, true);
      } catch (e) {
        console.error("Table doesn't support dialog view: " + e.message);
      }

    });
  });
}

export { populateTable, initTable };
