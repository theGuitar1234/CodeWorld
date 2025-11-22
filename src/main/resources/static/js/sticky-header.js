const headerSticky = document.querySelector("header");
const navSticky = document.querySelector("nav");

let height = window.getComputedStyle(headerSticky).getPropertyValue("height");
navSticky.style.setProperty('--height', `${height}`);
