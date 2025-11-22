const dialog = document.getElementById("transaction-dialog");
const tableBody = document.querySelector("tbody");
const wrapper = document.getElementById("dialog-wrapper");

const dataTotal = document.getElementById("data-total");
const dataTotalHeader = document.getElementById("data-total-header");
const dataPaidBy = document.getElementById("data-paid-by");
const dataTransactionId = document.getElementById("data-transaction-id");
const dataDescription = document.getElementById("data-description");
const dataStatus = document.getElementById("data-status");
const dataFee = document.getElementById("data-fee");
const dataAmount = document.getElementById("data-amount");
const dataDate = document.getElementById("data-date");
const dataPaidByHeader = document.getElementById("data-paid-by-header");

let row;

function showDialog(show) {
  if (show) {
    dialog.showModal();
    requestAnimationFrame(() => {
        dialog.classList.add("is-open");
    });
  } else {
    dialog.classList.remove("is-open");
    dialog.close();
  } 
}

tableBody.addEventListener("click", (e) => {

  const row = e.target.closest("tr");
  if (!row) return;

  dataTotal.textContent         = row.dataset.total;
  dataTotalHeader.textContent   = row.dataset.total;
  dataPaidByHeader.textContent  = row.dataset.paidBy;
  dataPaidBy.textContent        = row.dataset.paidBy;
  dataDate.textContent          = row.dataset.date;
  dataAmount.textContent        = row.dataset.amount;
  dataFee.textContent           = row.dataset.fee;
  dataTransactionId.textContent = row.dataset.transactionId;
  dataDescription.textContent   = row.dataset.description;
  dataStatus.textContent        = row.dataset.status;

  showDialog(true);
});

dialog.addEventListener("click", (e) => {
    if (!wrapper.contains(e.target)) showDialog(false);
});
