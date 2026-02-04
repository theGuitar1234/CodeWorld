let paginationTableRoot, paginationFilterRoot, paginationLinkRoot;

const tableRootSelector = "tbody[pagination-table-root]";
const linkRootSelector = "[pagination-nav-root]";
const filterRootSelector = "[pagination-filter-root]";
const linkPaginationSelector = "a[data-pagination]";

function cacheElements() {
  paginationTableRoot = document.querySelector(tableRootSelector);
  paginationFilterRoot = document.querySelector(filterRootSelector);
  paginationLinkRoot = document.querySelector(linkRootSelector);

  console.log(paginationFilterRoot);
  console.log(paginationLinkRoot);
  console.log(paginationTableRoot);
}

async function handleFragment(fetchUrl, { push = true } = {}) {
  try {
    
    const loading = document.createElement("div");
    loading.classList.add("loading");
    loading.textContent = 'loading';
    document.body.appendChild(loading);

    //await new Promise(r => setTimeout(r, 1000));

    const res = await fetch(fetchUrl, { credentials: "same-origin" });

    if (!res.ok) return;

    const html = await res.text();

    console.log(html);

    const doc = new DOMParser().parseFromString(html, "text/html");

    if (!doc) return;

    const oldPaginationFilterRoot = paginationFilterRoot.firstElementChild;
    const newPaginationFilterRoot = doc.querySelector(filterRootSelector);

    oldPaginationFilterRoot
      ? oldPaginationFilterRoot.replaceWith(newPaginationFilterRoot)
      : paginationFilterRoot.appendChild(newPaginationFilterRoot);

    const oldPaginationTableRoot = paginationTableRoot;
    const newPaginationTableRoot = doc.querySelector(tableRootSelector);
    oldPaginationTableRoot.replaceChildren(...newPaginationTableRoot.children);
    // oldPaginationTableRoot.replaceWith(newPaginationTableRoot);
    // oldPaginationTableRoot = newPaginationTableRoot;

    const oldPaginationLinkRoot = paginationLinkRoot.firstElementChild;
    let newPaginationLinkRoot = doc.querySelector(linkRootSelector);

    if (!newPaginationLinkRoot)
      newPaginationLinkRoot = document.createElement("div");

    oldPaginationLinkRoot
      ? oldPaginationLinkRoot.replaceWith(newPaginationLinkRoot)
      : paginationLinkRoot.appendChild(newPaginationLinkRoot);
    
    document.body.removeChild(loading);

  } catch (e) {
    alert(`Something went wrong while fetching ${e}`);
    console.error(e);
  }

  //mergedUrl.searchParams.set("fragment", "false");
  fetchUrl.searchParams.delete("fragment");
  if (push) history.pushState({}, "", fetchUrl.toString());

  document.dispatchEvent(
    new CustomEvent("pagination:navigated", { detail: { mergedUrl } }),
  );
}

function canRender() {
  return paginationFilterRoot && paginationLinkRoot && paginationTableRoot;
}

function handleClick(e) {
  const link = e.target.closest(linkPaginationSelector);
  if (!link) return;

  e.preventDefault();

  console.log("Successfully intercepted");

  const url = new URL(link.href, location.origin);

  console.log(url.href);

  const mergedUrl = new URL(window.location.href);
  mergedUrl.pathname = url.pathname;
  mergedUrl.hash = url.hash;

  for (const [key, value] of url.searchParams.entries()) {
    mergedUrl.searchParams.set(key, value);
  }

  const fetchUrl = new URL(mergedUrl);

  fetchUrl.searchParams.set("fragment", "true");
  fetchUrl.searchParams.set("mode", "VIEW_ALL");

  handleFragment(mergedUrl, { push: true });
}

function handlePopState() {
  handleFragment(new URL(window.location.href), { push: false });
}

function initPagination() {
  console.log("Initializing Fragment");

  cacheElements();

  if (!canRender()) return;

  console.log("Can render");

  window.addEventListener("popstate", handlePopState);
  document.addEventListener("click", (e) => {
    handleClick(e);
  });
}

export { initPagination };
