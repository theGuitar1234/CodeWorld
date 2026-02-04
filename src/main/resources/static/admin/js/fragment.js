let spaRoot;

function cacheElements() {
  spaRoot = document.querySelector("[data-spa-root]");
}

async function navigate(url, { push = true } = {}) {
  const mergedUrl = new URL(url, location.origin);

  const fetchUrl = new URL(mergedUrl);
  fetchUrl.searchParams.set("fragment", "true");

  const loading = document.createElement("div");
  loading.classList.add("loading");
  loading.textContent = 'loading';
  document.body.appendChild(loading);

  await new Promise(r => setTimeout(r, 1000));

  const res = await fetch(fetchUrl, { credentials: "same-origin" });
  if (!res.ok) return;

  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, "text/html");
  const incomingRoot =
    doc.querySelector("[data-spa-root]") || doc.querySelector("main");

  if (!incomingRoot) return;

  spaRoot.replaceWith(incomingRoot);
  spaRoot = incomingRoot;

  document.body.removeChild(loading);

  mergedUrl.searchParams.delete("fragment");

  if (push) history.pushState({}, "", mergedUrl.toString());

  document.dispatchEvent(
    new CustomEvent("spa:navigated", { detail: { url: mergedUrl } })
  );
}

function onClick(e) {
  const link = e.target.closest("a[data-spa]");

  if (
    !link ||
    e.metaKey ||
    e.ctrlKey ||
    e.shiftKey ||
    e.altKey ||
    (link.target && link.target !== "_self")
  ) return;

  const url = new URL(link.href, location.origin);
  if (url.origin !== location.origin) return;

  e.preventDefault();
  navigate(url, { push: true });
}

function onPopState() {
  navigate(location.href, { push: false });
}

async function handleFragmentForm(e) {
  const form = e.target.closest("form[data-spa-form]");
  if (!form) return;

  e.preventDefault();

  const url = new URL(form.action, location.origin);
  url.searchParams.set("fragment", "true");

  const body = new URLSearchParams(new FormData(form));

  const token = document.querySelector('meta[name="_csrf"]').content;
  const header = document.querySelector('meta[name="_csrf_header"]').content;

  const loading = document.createElement("div");
  loading.classList.add("loading");
  loading.textContent = 'loading';
  document.body.appendChild(loading);

  await new Promise(r => setTimeout(r, 1000));

  const res = await fetch(url, {
    method: "POST",
    headers: {
      [header]: token,
      "X-Requested-With": "XMLHttpRequest",
      "X-SPA": "true"
    },
    body,
    credentials: "same-origin",
  });

  if (!res.ok) return;

  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, "text/html");
  const incomingRoot = doc.querySelector("[data-spa-root]");
  if (!incomingRoot) return;

  spaRoot.replaceWith(incomingRoot);
  spaRoot = incomingRoot;

  document.body.removeChild(loading);

  document.dispatchEvent(new CustomEvent("spa:navigated", { detail: { url } }));
}

function initFragment() {
  cacheElements();
  if (!spaRoot) return;

  window.addEventListener("popstate", onPopState);
  document.addEventListener("click", onClick);
  document.addEventListener("submit", handleFragmentForm);
}

export { initFragment };
