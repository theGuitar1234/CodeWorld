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

  const row = btn.closest("tr");
  row?.remove();

  const url = btn.dataset.url;
  if (!url) return;

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(url, {
      method: "DELETE",
      headers: {
        [header]: token,
        "X-Requested-With": "XMLHttpRequest",
        "X-SPA": "true"
      },
      credentials: "same-origin",
    });

    if (!res.ok) throw new Error(`Fetch Failed: ${res.status}`);

    const html = await res.text();

    const doc = new DOMParser().parseFromString(html, "text/html");
    const incomingRoot = doc.querySelector("[body]");
    const spaRoot = document.querySelector("[data-spa-root]") || doc.querySelector("main");

    if (!incomingRoot) return

    console.log(incomingRoot);

    spaRoot.appendChild(incomingRoot);
    
  } catch (e) {
    btn.disabled = false;
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }

  document.dispatchEvent(new CustomEvent("spa:navigated", { detail: { url } }));
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