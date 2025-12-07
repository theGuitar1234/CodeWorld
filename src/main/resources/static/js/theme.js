let 
    root,
    theme,
    layers;

const 
    key = "theme";

function cacheElements() {
    root = document.documentElement;
    theme = document.getElementById("theme");
    layers = document.querySelector(".layer");
}

function handleTheme() {
    const saved = localStorage.getItem(key);
    const systemDark = matchMedia("(prefers-color-scheme: dark)").matches;
    const initial = saved ?? (systemDark ? "dark" : "light");

    root.dataset.theme = initial;
    initial === "dark" ? layers.style.mixBlendMode = 'normal' : layers.style.mixBlendMode = 'multiply';
    theme.checked = saved === "dark";

    theme.addEventListener("change", () => {
        const next = theme.checked ? "dark" : "light";
        root.dataset.theme = next;
        next === "dark" ? layers.style.mixBlendMode = 'normal' : layers.style.mixBlendMode = 'multiply';
        localStorage.setItem(key, next);
    });
}

function initTheme() {

  cacheElements();

  if (!root || !theme) return;

  handleTheme();
}

export { initTheme };
