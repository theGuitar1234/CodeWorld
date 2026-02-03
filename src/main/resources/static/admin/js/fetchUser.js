const selectedStudentIds = new Set();

let 
  isBound = false;

function syncCheckboxes(container = document) {
  container
    .querySelectorAll('input.js-student[type="checkbox"]')
    .forEach((c) => {
      const id = c.dataset.id;
      c.checked = selectedStudentIds.has(id);
    });
}

function updateSelected() {
  const selected = document.querySelector("#selectedCount");
  
  if (!selected) return;

  selected.textContent =
    `Selected ${selectedStudentIds.size} Student(s)`;
}

function showDialog(dialog, show) {
  if (!dialog) return;

  if (!dialog.isConnected) document.body.appendChild(dialog);

  show ? dialog.showModal() : dialog.close();
}

function handleSubmit(e) {
  const form = e.target.closest(".courseOfferingForm");
  if (!form) return;

  form
    .querySelectorAll("input[name='studentIds']")
    .forEach((input) => input.remove());

  for (const id of selectedStudentIds) {
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "studentIds";
    input.value = String(id);
    form.appendChild(input);
  }
}

function handleChange(e) {
  const checkbox = e.target.closest('input.js-student[type="checkbox"]');
  if (!checkbox) return;

  const id = checkbox.dataset.id;
  checkbox.checked ? selectedStudentIds.add(id) : selectedStudentIds.delete(id);

  updateSelected();
}

async function handleStudentIds(e) {
  const a = e.target.closest("a.js-page");
  if (!a) return;

  e.preventDefault();

  const url = new URL(a.href, window.location.origin);
  url.searchParams.set("fragment", "true");

  url.searchParams.set(
    "userId", document.querySelector('input[name="userId"]').value,
  );

  console.log(url.href);

  const token = document.querySelector('meta[name="_csrf"]').content;
  const header = document.querySelector('meta[name="_csrf_header"]').content;

  try {
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
    const incomingTableBody = doc.querySelector("tbody[dataCourseOffering");
    const tbody = document.querySelector("dialog[courseOffering] table tbody");

    if (incomingTableBody && tbody) {
      tbody.replaceWith(incomingTableBody);
      syncCheckboxes(document);
    }

    const incomingPaginationNav = doc.querySelector(
      "#pagination-container nav",
    );
    const paginationNav = document.querySelector("#pagination-container nav");

    if (incomingPaginationNav && paginationNav) {
      paginationNav.replaceWith(incomingPaginationNav);
    }
  } catch (e) {
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }
}

function handleDialog(e) {
  const openBtn = e.target.closest("button[dialog]");
  if (openBtn) {
    const name = openBtn.getAttribute("dialog");
    const dialog = document.querySelector(`dialog[${name}]`);
    if (dialog) showDialog(dialog, true);
    return;
  }

  if (e.target.closest("[clsBttn]")) {
    const dialog = e.target.closest("dialog");
    if (dialog) showDialog(dialog, false);
    return;
  }

  const dialog = e.target.closest("dialog");
  if (!dialog) return;

  if (e.target === dialog) showDialog(dialog, false);
}

function initFetchUser() {

  console.log("Initializing Fetch User");

  if (isBound) return;
  isBound = true;

  document.addEventListener("submit", (e) => handleSubmit(e));
  document.addEventListener("change", (e) => handleChange(e));
  document.addEventListener("click", async (e) => handleStudentIds(e));
  document.addEventListener("click", (e) => handleDialog(e));

  document.addEventListener(
    "cancel",
    (e) => {
      const d = e.target.closest?.("dialog");
      if (!d) return;
      e.preventDefault();
      showDialog(d, false);
    },
    true,
  );

  syncCheckboxes();
  updateSelected();
}

export { initFetchUser };
