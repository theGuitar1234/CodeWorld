let 
  dialog,
  isBound = false;

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
    return;
  }

  d.classList.remove("is-open");

  const finish = () => {
    d.removeEventListener("transitionend", finish, { once: true });
    if (d.open) d.close();
  }

  d.addEventListener("transitionend", finish, { once: true });

  setTimeout(() => {
    if (d.open) d.close();
  }, 450);
}

function handleDialogWrapper() {
  if (!dialog) return;
  dialog.forEach(d => d.addEventListener("click", (e) => {
    if (!d.firstElementChild.contains(e.target)) showDialog(d, false);

    if (e.target.closest("[clsBttn]")) showDialog(d, false);
  }));
}

function initDialog(dialogSelector = "[transaction-dialog]") {

  if (isBound) return;
  isBound = true;

  cacheElements(dialogSelector);

  if (!dialog) return;

  handleDialogWrapper();

  document.addEventListener("cancel", (e) => {
    const d = e.target.closest?.(dialogSelector);
    if (!d) return;
    e.preventDefault();
    showDialog(d, false);
  }, true);
}

export { initDialog, showDialog };
