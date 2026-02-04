// let isBound = false;

async function handleContact(e) {
  const contactLink = e.target.closest("a[contactLink]");

  if (!contactLink) return;

  e.preventDefault();

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const url = new URL(contactLink.href, location.origin);

    const res = await fetch(url, {
      method: "POST",
      headers: {
        [header]: token,
        "X-Requested-With": "XMLHttpRequest",
        "X-SPA": "true",
      },
      credentials: "same-origin",
    });

    if (!res.ok) return;

    const html = await res.text();

    const doc = new DOMParser().parseFromString(html, "text/html");
    const incomingRoot = doc.querySelector("[body]");
    const spaRoot = document.querySelector("[data-spa-root]") || doc.querySelector("main");

    if (!incomingRoot) return;

    console.log(incomingRoot);

    spaRoot.appendChild(incomingRoot);

    // const mergedUrl = new URL(window.location.href);
    // mergedUrl.searchParams.set("success", output);

    // history.pushState({}, "", mergedUrl.toString());

    document.dispatchEvent(new CustomEvent("spa:navigated", { detail: { url } }));
  } catch (e) {
    alert(`Failed to contact ${e}`);
    console.error(e);
  }
}

// function initContact() {
//   if (isBound) return;
//   isBound = true;

//   document.addEventListener("click", async (e) => handleContact(e));
// }

export { handleContact };
