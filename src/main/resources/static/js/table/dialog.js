let dialog, wrapper;

function cacheElements(dialogSelector, wrapperSelector) {
  dialog = document.querySelector(dialogSelector);
  wrapper = document.querySelector(wrapperSelector);
}

function showDialog(show) {
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

function handleDialogWrapper() {
  if (!dialog || !wrapper) return;
  dialog.addEventListener("click", (e) => {
    if (!wrapper.contains(e.target)) showDialog(false);

    if (e.target.closest("[clsBttn]")) showDialog(false);
  });
}

function initDialog( 
    dialogSelector = "#transaction-dialog",
    wrapperSelector = "#dialog-wrapper") {

  cacheElements(dialogSelector, wrapperSelector);

  if (!dialog || !wrapper) return;

  handleDialogWrapper();
}

export { initDialog, showDialog };
