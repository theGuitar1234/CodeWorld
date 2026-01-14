let 
  paginationContainer,
  paginationFilterContainer,
  table;

function cacheElements() {
  paginationContainer = document.getElementById("pagination-container");
  paginationFilterContainer = document.getElementById("pagination-filter-container");
  table = document.querySelector("table");
}

async function handleFragment(e) {

  const link = e.target.closest("#view-all a, li a.js-page");

  if (!link) return;

  e.preventDefault();

  const url = new URL(link.href, location.origin);
  url.searchParams.set("fragment", "true");
  url.searchParams.set("mode", "VIEW_ALL");

  const res = await fetch(url);
  
  if (!res.ok) return;

  const html = await res.text();

  const tempDiv = document.createElement("div");
  tempDiv.innerHTML = html;

  paginationFilterContainer.replaceChild(tempDiv.querySelector("#transactions-filter"), paginationFilterContainer.firstElementChild);
  paginationContainer.replaceChild(tempDiv.querySelector("#pagination"), paginationContainer.firstElementChild);
  table.replaceChild(tempDiv.querySelector("tbody"), table.querySelector("tbody"));

  url.searchParams.set("fragment", "false");
  history.pushState({}, "", url.toString());
}

function initPagination() {

  console.log("Initializing Fragment");

  cacheElements();



  if (!paginationContainer || !table) return;

  document.addEventListener("click", (e) => {
    handleFragment(e);
  });
}

export { initPagination };
