let 
  paginationTableRoot,
  paginationFilterRoot,
  paginationLinkRoot;

const tableRootSelector = "tbody[pagination-table-root]";
const linkRootSelector = "[pagination-link-root]";
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

async function handleFragment(e, { push = true } = {}) {
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

  const res = await fetch(fetchUrl, { credentials: "same-origin" });
  
  if (!res.ok) return;

  const html = await res.text();

  const doc = new DOMParser().parseFromString(html, "text/html");

  if (!doc) return;

  const oldPaginationFilterRoot = paginationFilterRoot.firstElementChild;
  const newPaginationFilterRoot = doc.querySelector(filterRootSelector);
  
  (oldPaginationFilterRoot) ?
    oldPaginationFilterRoot.replaceWith(newPaginationFilterRoot) :  
    paginationFilterRoot.appendChild(newPaginationFilterRoot);

  const oldPaginationTableRoot = paginationTableRoot;
  const newPaginationTableRoot = doc.querySelector(tableRootSelector);
  oldPaginationTableRoot.replaceChildren(...newPaginationTableRoot.children);
  // oldPaginationTableRoot.replaceWith(newPaginationTableRoot);
  // oldPaginationTableRoot = newPaginationTableRoot;
  
  const oldPaginationLinkRoot = paginationLinkRoot.firstElementChild;
  const newPaginationLinkRoot = doc.querySelector(linkRootSelector);

  (oldPaginationLinkRoot) ?
    oldPaginationLinkRoot.replaceWith(newPaginationLinkRoot) :
    paginationLinkRoot.appendChild(newPaginationLinkRoot);

  //mergedUrl.searchParams.set("fragment", "false");
  mergedUrl.searchParams.delete("fragment");
  if (push) history.pushState({}, "", mergedUrl.toString());

  document.dispatchEvent(new CustomEvent("pagination:navigated", { detail: { mergedUrl } }));
}

function canRender() {
  return (
    paginationFilterRoot &&
    paginationLinkRoot &&
    paginationTableRoot
  );
}

function initPagination() {

  console.log("Initializing Fragment");

  cacheElements();

  if (!canRender()) return;

  console.log("Can render");

  // window.addEventListener("popstate", (e) => {
  //   handleFragment(e, { push: false });
  // });

  document.addEventListener("click", (e) => {
    handleFragment(e);
  });
}

export { initPagination };
