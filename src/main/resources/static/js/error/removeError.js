let isBound = false;

function removeError(e) {
  
  if (!e.target.closest("#error-button")) return;

  e.preventDefault();

  const box = document.getElementById("error");
  if (!box) return;

  box.remove();

  const url = new URL(window.location.href);
  url.searchParams.has("error") ? url.searchParams.delete("error") : "";
  history.replaceState({}, document.title, url);
}

function initRemoveError() {
    console.log("Initializing Remove Error");

    if (isBound) return;
    isBound = true;

    document.addEventListener("click", removeError);
}

export { initRemoveError }
