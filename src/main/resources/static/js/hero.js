let 
  hero, 
  layer1, 
  layer2,
  layer3,
  heroContainer;

function cacheElements() {
  hero = document.querySelector(".hero");
  layer1 = document.querySelector(".layer1");
  layer2 = document.querySelector(".layer2");
  layer3 = document.querySelector(".layer3");
  heroContainer = document.querySelector(".hero-container");
}

function shouldRun() {
  const isReducedMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
  ).matches;
  const isTouch = window.matchMedia("(pointer: coarse)").matches;
  return !isReducedMotion && !isTouch;
}

function clamp01(v) {
  return v < 0 ? 0 : v > 1 ? 1 : v;
}

function initHero() {
  cacheElements();
  if (!hero || !layer1 || !layer2 || !layer3 || !heroContainer || !shouldRun()) return;

  const layers = [
    { el: heroContainer, maxOffset: 8, ease: 0.14, x: 0, y: 0, tx: 0, ty: 0 },
    { el: layer1, maxOffset: 14, ease: 0.12, x: 0, y: 0, tx: 0, ty: 0 },
    { el: layer2, maxOffset: 14, ease: 0.12, x: 0, y: 0, tx: 0, ty: 0 },
    { el: layer3, maxOffset: 14, ease: 0.12, x: 0, y: 0, tx: 0, ty: 0 },
  ];

  for (const L of layers) L.el.style.willChange = "transform";

  const SPEED_FOR_MAX = 1400;
  const smooth = 0.25;

  let last = null;
  let svx = 0,
    svy = 0;

  const rawEvent =
    "onpointerrawupdate" in window ? "pointerrawupdate" : "pointermove";

  function onMove(e) {
    const now = performance.now();
    const x = e.clientX;
    const y = e.clientY;

    if (!last) {
      last = { x, y, t: now };
      return;
    }

    const dx = x - last.x;
    const dy = y - last.y;
    const dt = (now - last.t) / 1000;

    last = { x, y, t: now };

    if (dt <= 0 || dt > 0.2) return;

    const vx = dx / dt;
    const vy = dy / dt;

    svx = svx + (vx - svx) * (1 - smooth);
    svy = svy + (vy - svy) * (1 - smooth);

    const speed = Math.hypot(svx, svy);

    if (speed < 1) {
      for (const L of layers) {
        L.tx = 0;
        L.ty = 0;
      }
      return;
    }

    const ux = svx / speed;
    const uy = svy / speed;

    const t = clamp01(speed / SPEED_FOR_MAX);

    for (const L of layers) {
      const mag = t * L.maxOffset;
      L.tx = ux * mag;
      L.ty = uy * mag;
    }
  }

  hero.addEventListener(rawEvent, onMove, { passive: true });

  hero.addEventListener("pointerleave", () => {
    last = null;
    svx = 0;
    svy = 0;
    for (const L of layers) {
      L.tx = 0;
      L.ty = 0;
    }
  });

  function render() {
    for (const L of layers) {
      L.x += (L.tx - L.x) * L.ease;
      L.y += (L.ty - L.y) * L.ease;
      L.el.style.setProperty("--mx", `${L.x.toFixed(2)}px`);
      L.el.style.setProperty("--my", `${L.y.toFixed(2)}px`);
    }
    requestAnimationFrame(render);
  }
  requestAnimationFrame(render);
}

export { initHero };
