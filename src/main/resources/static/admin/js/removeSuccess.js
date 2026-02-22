let isBound = false;

function removeSuccess(e) {
  if (!e.target.closest("#success-button")) return;

  e.preventDefault();
  const box = document.getElementById("success");
  if (!box) return;
  box.remove();

  const url = new URL(window.location.href);
  url.searchParams.has("success") ? url.searchParams.delete("success") : "";
  history.replaceState({}, document.title, url);
}

function initRemoveSuccess() {
  console.log("Initializing Remove Success");

  if (isBound) return;
  isBound = true;

  document.addEventListener("click", removeSuccess);
}

export { initRemoveSuccess };
