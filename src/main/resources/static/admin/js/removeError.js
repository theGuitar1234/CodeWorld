let 
    errorButton;

function cacheElements() {
    errorButton = document.querySelector("#error-button");
}

function removeError() {
  document.getElementById("error").remove();
  const url = new URL(window.location.href);
  url.searchParams.has("error") ? url.searchParams.delete("error") : "";
  history.replaceState({}, document.title, url);
}

function initRemoveError() {
    console.log("Initializing Remove Error");

    cacheElements();

    if (!errorButton) return;

    errorButton.addEventListener("click", removeError);
}

export { initRemoveError }
