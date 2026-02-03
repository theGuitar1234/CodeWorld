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
        "X-SPA": "true"
      },
      credentials: "same-origin",
    });

    if (!res.ok) return;

    const html = await res.text();

    console.log(html);

    const doc = new DOMParser().parseFromString(html, "text/html");
    const incomingRoot = doc.querySelector("[body]");

    if (!incomingRoot) return;

    console.log(incomingRoot);

    document.body.appendChild(incomingRoot);
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

export { handleContact }
