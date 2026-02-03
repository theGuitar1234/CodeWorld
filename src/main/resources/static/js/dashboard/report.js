let statement;

function cacheElements() {
  statement = document.querySelector("#statement");
}

function JSONToCSV(json, delimiter = ",") {
  const BOM = "\uFEFF";

  //console.log(json.content);
  const rows = json ?? [];
  if (!rows || rows.length == 0) return;

  const escapeCell = (value) => {
    if (value === null || value === undefined) return "";

    const str = String(value);

    if (
      str.includes('"') ||
      str.includes("\n") ||
      str.includes("\r") ||
      str.includes(delimiter)
    )
      return `"${str.replace(/"/g, '""')}"`;

    return str;
  };

  const data = [];

  const headers = Object.keys(rows[0]).map((h) => escapeCell(h));

  data.push(headers.join(delimiter));

  for (const row of rows) {
    data.push(headers.map((h) => escapeCell(row[h])).join(delimiter));
  }

  return BOM + data.join("\r\n");
}

function downloadCSVReport(data) {
  const csv = JSONToCSV(data);

  const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);

  const a = document.createElement("a");
  a.href = url;
  a.download = "report.csv";
  a.click();

  a.remove();
  URL.revokeObjectURL(url);
}

function flattenJSON(json, prefix = "", out = {}) {
  for (const [key, value] of Object.entries(json)) {
    const fullKey = prefix ? `${prefix}.${key}` : key;

    if (Array.isArray(value)) {
      out[fullKey] = value
        .map((v) => (typeof v === "object" ? JSON.stringify(v) : String(v)))
        .join("|");
    } else if (value && typeof value === "object") {
      flattenJSON(value, fullKey, out);
    } else {
      out[fullKey] = value;
    }
  }
  return out;
}

async function handleReport(e) {

  e.preventDefault();

  try {

    const url = new URL(statement.href, location.origin);

    const currentUrl = new URL(window.location.href);

    for (const [key, value] of currentUrl.searchParams.entries()) {
        url.searchParams.set(key, value);
    }
    
    const res = await fetch(url, { 
      credentials: "same-origin", 
      headers: { 
        "X-Requested-With": "XMLHttpRequest",
        "X-SPA": "true"
      }
    });
    const data = await res.json();

    console.log(data);

    downloadCSVReport(data.map(d => flattenJSON(d)));

  } catch (e) {
    alert(`Something Went Wrong : ${e}`);
    console.error(`Something Went Wrong : ${e}`);
  }
}

function initReport() {
  console.log("Initializing Report");

  cacheElements();

  if (!statement) return;

  statement.addEventListener("click", (e) => handleReport(e));
}

export { initReport };
