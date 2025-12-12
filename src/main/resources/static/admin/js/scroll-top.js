let header,
  nav,
  backToTop,
  lastScrollY = 0;

function cacheElements() {
  header = document.querySelector("header");
  nav = document.querySelector("header nav");
  backToTop = document.querySelector(".anchor");
}

function handleScroll() {
  const currentScrollY = window.scrollY;
  if (currentScrollY > lastScrollY) {
    header.classList.add("reveal");
    backToTop?.classList.add("show");
  } else {
    header.classList.remove("reveal");
    backToTop?.classList.remove("show");
  }
  lastScrollY = currentScrollY;
}

function initScrollTop() {

  console.log("Scroll Top initialized");

  cacheElements();

  if (!header) return;

  lastScrollY = window.scrollY;

  window.addEventListener("scroll", handleScroll, { passive: true });
}

export { initScrollTop };
