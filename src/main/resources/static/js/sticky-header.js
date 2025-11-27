let 
  headerSticky,
  navSticky,
  height;

function cacheElements() {
  headerSticky = document.querySelector("header");
  navSticky = document.querySelector("nav");
}

function updateNavHeight() {
  if (!headerSticky || !navSticky) return;

  height = window.getComputedStyle(headerSticky).getPropertyValue("height");
  navSticky.style.setProperty("--height", `${height}`);
}

function initStickyHeader() {
  cacheElements();
  updateNavHeight();
  window.addEventListener("resize", updateNavHeight);
}

export { initStickyHeader };
