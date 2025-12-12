let 
    body,
    collapse,
    aside

function cacheElements() {
    body = document.body;
    collapse = document.getElementById("collapse");
    aside = document.querySelector("aside");
}

function handleExpandAside() {
    body.classList.toggle("expand");
    aside.classList.toggle("show");
}

function initExpand() {

    console.log("Expand initialized");

    cacheElements();

    if (!body || !collapse || !aside) return;

    collapse.addEventListener("change", handleExpandAside);
}

export { initExpand };