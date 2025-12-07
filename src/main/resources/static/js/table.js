import { showDialog } from "./dialog.js";

const 
  opt1 = "checked",
  opt2 = "rejected",
  opt3 = "pending";

let 
  dialogStatusIcon,

  tableBody,
  table,

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
  status;

function cacheElements() {
  dialogStatusIcon = document.getElementById("dialog-status-icon");

  tableBody = document.querySelector("tbody");
  table = document.querySelector("table");

  dataTotal = document.getElementById("data-total");
  dataTotalHeader = document.getElementById("data-total-header");
  dataPaidBy = document.getElementById("data-paid-by");
  dataTransactionId = document.getElementById("data-transaction-id");
  dataDetails = document.getElementById("data-details");
  dataStatus = document.getElementById("data-status");
  dataFee = document.getElementById("data-fee");
  dataAmount = document.getElementById("data-amount");
  dataDate = document.getElementById("data-date");
  dataPaidByHeader = document.getElementById("data-paid-by-header");
}

function resetDialogStatusClasses() {
  if (!dialogStatusIcon) return;
  dialogStatusIcon.classList.remove(opt1, opt2, opt3);
}

function populateDialog(row) {
  if (!row) return;

  resetDialogStatusClasses();

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
  if (!table) cacheElements();
  if (!table) return;

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
}

function initTable() {
  cacheElements();

  if (!tableBody) return;

  tableBody.addEventListener("click", (e) => {
    row = e.target.closest("tr");
    if (!row) return;
    populateDialog(row);
    showDialog(true);
  });
}

export {
  populateTable,
  initTable,
};
