let lastScrollY = window.scrollY;
const header = document.querySelector("header");
const nav = document.querySelector("header nav")
const backToTop = document.querySelector(".anchor");

window.addEventListener("scroll", () => {
    const currentScrollY = window.scrollY;
    if (currentScrollY > lastScrollY) {
        header.classList.add("reveal");
        backToTop.classList.add('show');
    } else {
        header.classList.remove('reveal');
        backToTop.classList.remove('show');
    }
    lastScrollY = currentScrollY;
}, { passive: true });