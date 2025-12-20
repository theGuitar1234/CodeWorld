let
  backToTop,
  lastScrollY = 0;

function cacheElements() {
  backToTop = document.querySelector(".anchor");
}

function handleScroll() {
  const currentScrollY = window.scrollY;
  if (currentScrollY > lastScrollY) {
    backToTop?.classList.add("show");
  } else {
    backToTop?.classList.remove("show");
  }
  lastScrollY = currentScrollY;
}

function initScrollTop() {

  cacheElements();

  if (!backToTop) return;

  lastScrollY = window.scrollY;

  window.addEventListener("scroll", handleScroll, { passive: true });
}

export { initScrollTop };
