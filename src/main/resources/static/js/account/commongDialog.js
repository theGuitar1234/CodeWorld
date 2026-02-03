let isBound = false;

function cacheElements() {
    const commonDialogs = document.querySelectorAll("dialog[commonDialog]");
    commonDialogs.forEach(d => {
        d.addEventListener("click", (e) => {
          if (e.target.closest("button[clsBttn]")) showDialog(d, false);
          if (!d.querySelector("#wrapper").contains(e.target)) showDialog(d, false);
        });
    });
}

function handleCommonDialog(e) {
  const commonDialogButton = e.target.closest("[commonDialogButton]");

  if (!commonDialogButton) return;

  showDialog(document.querySelector(`dialog[${commonDialogButton.getAttribute("dialog")}]`), true);
}

function showDialog(dialog, show) {
  if (!dialog) return;

  if (!dialog.isConnected) document.body.appendChild(dialog);

  if (show) {
    dialog.showModal();
    dialog.classList.add("is-open");
  } else {
    dialog.classList.remove("is-open");
    dialog.close();
  }
}

function initCommonDialog() {

  console.log("Initializing Common Dialogs");

  if (isBound) return;
  isBound = true;

  cacheElements();

  document.addEventListener("click", (e) => handleCommonDialog(e));
  document.addEventListener(
    "cancel",
    (e) => {
      const d = e.target.closest?.("dialog");
      if (!d) return;
      e.preventDefault();
      showDialog(d, false);
    },
    true,
  );
}

export { initCommonDialog };
