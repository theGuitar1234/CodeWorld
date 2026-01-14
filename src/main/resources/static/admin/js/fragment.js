let spaRoot;

function cacheElements() {
  spaRoot = document.querySelector("[data-spa-root]");
}

async function handleFragment(e, { push = true } = {}) {

  const link = e.target.closest("#view-all a, li a.js-page, a[data-spa]");

  if (
    !link || 
    e.metaKey ||
    e.ctrlKey ||
    e.shiftKey ||
    e.altKey ||
    link.target && link.target !== "_self"
  ) return;

  e.preventDefault();

  const url = new URL(link.href, location.origin);

  if (url.origin !== location.origin) return;

  url.searchParams.set("fragment", "true");
  url.searchParams.set("mode", "VIEW_ALL");

  const res = await fetch(url, {credentials: "same-origin"});

  if (!res.ok) return;

  const html = await res.text();

  const doc = new DOMParser().parseFromString(html, "text/html");
  const incomingRoot = doc.querySelector("[data-spa-root]") || doc.querySelector(".wrapper");

  if (!incomingRoot) return;

  spaRoot.replaceWith(incomingRoot);
  spaRoot = incomingRoot;

  url.searchParams.set("fragment", "false");
  if (push) history.pushState({}, "", url.toString());

  document.dispatchEvent( new CustomEvent("spa:navigated", {detail: { url }}));
}

function initFragment() {
  console.log("Initializing Fragment...");

  cacheElements();

  if (!spaRoot) return;

  window.addEventListener("popstate", () => {
    handleFragment(e, { push: false });
  });

  document.addEventListener("click", (e) => {
    handleFragment(e);
  });
}

export { initFragment };
