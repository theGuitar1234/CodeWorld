let 
  dialogs;

function cacheElements(dialogSelector) {
  dialogs = document.querySelectorAll(dialogSelector);
}

function showDialog(dialog, show) {
  if (!dialog) return;
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

function handleDialogWrapper(dialogs) {

  if (!dialogs) return;

  console.log("Starting to add the event listeners");

  console.log(dialogs);

  dialogs.forEach(d => {
    d.addEventListener("click", (e) => {
      if (!d.firstElementChild.contains(e.target)) showDialog(d, false);
      if (e.target.closest("[clsBttn]")) showDialog(d, false);
    });
  });
}

function initTransactionDialogs(dialogSelector = "dialog[transaction-dialog]") {

  console.log("Initializing Dialog..")

  cacheElements(dialogSelector);

  if (!dialogs || dialogs.length === 0) return;

  handleDialogWrapper(dialogs);

}

export { initTransactionDialogs, showDialog };
