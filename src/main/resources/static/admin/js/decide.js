let 
  requestsTable,
  isBound = false;

function cacheElements() {
  requestsTable = document.querySelector("[requests-table]");
}

async function handleDecide(e) {
  const btn = e.target
  if (!btn) return;

  e.preventDefault();

  btn.disabled = true;

  const url = btn.dataset.url;
  if (!url) return;

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(url, {
      method: "DELETE",
      headers: {
        [header]: token,
        "X-Requested-With": "fetch",
      },
      credentials: "same-origin",
    });

    if (!res.ok) throw new Error(`Fetch Failed: ${res.status}`);

    const row = btn.closest("tr");
    row?.remove();
  } catch (e) {
    btn.disabled = false;
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }
}

function initDecide() {

  if (isBound) return;
  isBound = true;
  
  cacheElements();

  if (!requestsTable) return;

  requestsTable.addEventListener("click", async (e) => {
    if (e.target.getAttribute("class") === "js-accept" ||
        e.target.getAttribute("class") === "js-reject") {
        handleDecide(e);
    }
  });
}

export { initDecide };