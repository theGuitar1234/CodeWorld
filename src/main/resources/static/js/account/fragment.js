let section, target, url, html;

async function handleFragmentDisplay(e) {
  const link = e.target.closest("a[section]");

  if (
    !link ||
    (link.target && link.target !== "_self") ||
    e.metaKey ||
    e.ctrlKey ||
    e.shiftKey ||
    e.altKey
  )
    return;

  e.preventDefault();

  url = new URL(window.location.href);

  if (url.origin !== location.origin) return;

  section = link.getAttribute("section");

  url.searchParams.set("fragment", "true");
  url.searchParams.set(section, "EDIT");
  url.searchParams.set("section", section);

  const loading = document.createElement("div");
  loading.classList.add("loading");
  loading.textContent = 'loading';
  document.body.appendChild(loading);
  console.log(document.querySelector(".loading"));

  const res = await fetch(url, { credentials: "same-origin" });
  if (!res.ok) return;

  html = await res.text();

  const doc = new DOMParser().parseFromString(html, "text/html");

  const selector = `[fragment="${CSS.escape(section)}"]`;
  // const incomingRoot = doc.querySelector(`[fragment = ${section}]`);
  const incomingRoot = doc.querySelector(selector);

  if (!incomingRoot) return;

  // target = document.querySelector(`[fragment = ${section}]`);
  target = document.querySelector(selector);

  target.replaceWith(incomingRoot);
  target = incomingRoot;

  document.body.removeChild(loading);

  url.searchParams.set("fragment", "false");
  history.pushState({}, "", url.toString());
}

async function handleFragmentEdit(e) {
  const form = e.target.closest("form[fragmentForm]");
  console.log(form);

  if (!form || e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;

  e.preventDefault();

  section = form.getAttribute("fragment");

  url = new URL(form.getAttribute("action"), location.origin);

  if (url.origin !== location.origin) return;

  url.searchParams.set("section", section);
  url.searchParams.set(section, "DISPLAY");

  try {

    const loading = document.createElement("div");
    loading.classList.add("loading");
    loading.textContent = 'loading';
    document.body.appendChild(loading);
    console.log(document.querySelector(".loading"));

    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const formFields = new FormData(form);
    const body = new URLSearchParams();

    for (const [key, value] of formFields.entries()) {
      if (typeof value === "string" && value.trim() === "") continue;
      body.append(key, value);
    }

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

    html = await res.text();

    document.body.removeChild(loading);
  } catch (e) {
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }

  const doc = new DOMParser().parseFromString(html, "text/html");

  const selector = `[fragment="${CSS.escape(section)}"]`;
  const incomingRoot = doc.querySelector(selector);

  console.log(incomingRoot);

  if (!incomingRoot) return;

  target = document.querySelector(selector);

  target.replaceWith(incomingRoot);
  target = incomingRoot;

  url = new URL(window.location.href);

  url.searchParams.set("fragment", "false");
  url.searchParams.delete(section, "EDIT");
  url.searchParams.delete("section", section);

  history.pushState({}, "", url.toString());
}

function initFragment() {
  console.log("Initializing Fragment...HO");

  document.addEventListener("click", (e) => handleFragmentDisplay(e));
  document.addEventListener("submit", (e) => handleFragmentEdit(e));
}

export { initFragment };
