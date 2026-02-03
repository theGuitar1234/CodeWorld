let 
    successButton;

function cacheElements() {
    successButton = document.querySelector("#success-button");
}

function removeSuccess() {
  console.log("i clicked on this");
  document.getElementById("success").remove();
  const url = new URL(window.location.href);
  url.searchParams.has("success") ? url.searchParams.delete("success") : "";
  history.replaceState({}, document.title, url);
}

function initRemoveSuccess() {
    console.log("Initializing Remove Success");

    cacheElements();

    document.addEventListener("click", removeSuccess);
}

export { initRemoveSuccess }
