let dialog;

function cacheElements(dialogSelector) {
  dialog = document.querySelectorAll(dialogSelector);
}

function showDialog(d, show) {
  if (!d) return;
  if (show) {
    d.showModal();
    requestAnimationFrame(() => {
      d.classList.add("is-open");
    });
  } else {
    d.classList.remove("is-open");
    d.close();
  }
}

function handleDialogWrapper() {
  if (!dialog) return;
  dialog.forEach(d => d.addEventListener("click", (e) => {
    if (!d.firstElementChild.contains(e.target)) showDialog(d, false);

    if (e.target.closest("[clsBttn]")) showDialog(d, false);
  }));
}

function initDialog(dialogSelector = "[transaction-dialog]") {

  cacheElements(dialogSelector);

  if (!dialog) return;

  handleDialogWrapper();
}

export { initDialog, showDialog };
