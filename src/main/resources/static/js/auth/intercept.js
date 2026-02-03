let 
  authForm,
  resend,
  url,
  timer,
  submit,
  countDownId = null;

const RESEND_MS = 2 * 60 * 1000;
const KEY = "resend";

function cacheElements() {
  authForm = document.querySelector("form[authForm]");
  resend = document.querySelector("a[resend]");
}

function getRemainingMs() {
  const until = Number(localStorage.getItem(KEY)) || 0;
  return Math.max(0, until - Date.now());
}

function startResendTimer(ms = RESEND_MS) {
  const until = Date.now() + ms;
  localStorage.setItem(KEY, String(until));
  return until;
}

function clearResendTimer() {
  localStorage.removeItem(KEY);
}

function startCountDown() {
  const remainingMs = getRemainingMs();

  if (remainingMs <= 0) {
    if (countDownId) clearInterval(countDownId);
    countDownId = null;

    clearResendTimer();
    timer.textContent = "";
    if (submit) submit.disabled = false;
    if (resend) resend.disabled = false;
    return;
  }

  if (submit) submit.disabled = true;
  if (resend) resend.disabled = true;

  const totalSeconds = Math.floor(remainingMs / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;

  timer.textContent = `Resend in : ${minutes}:${String(seconds).padStart(2, "0")}`;
}

function startOrResumeCountDown() {
  if (countDownId) clearInterval(countDownId);

  startCountDown();

  if (getRemainingMs() > 0) {
    countDownId = setInterval(startCountDown, 250);
  }
}

async function handleInterceptForm(e) {
  e.preventDefault();
  console.log("I see you tried to submit this form but I intercepted hahahaha");

  if (getRemainingMs() > 0) return;

  url = new URL(authForm.getAttribute("action"), location.origin);

  if (url.origin !== location.origin) return;

  const body = new URLSearchParams(new FormData(authForm));

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(url, {
      method: "POST",
      headers: {
        [header]: token,
        "X-Requested-With": "XMLHttpRequest",
        "X-SPA": "true"
      },
      body,
      credentials: "same-origin",
    });

    if (!res.ok) throw new Error(`Fetch Failed: ${res.status}`);

    startResendTimer(RESEND_MS);
    startOrResumeCountDown();

    const result = await res.text();
    console.log(result);
  } catch (e) {
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }
}

async function handleInterceptLink(e) {
  e.preventDefault();
  console.log("I see you tried to click this link but I intercepted hahahaha");

  if (getRemainingMs() > 0) return;

  console.log(resend.href);

  url = new URL(resend.href, location.origin);

  if (url.origin !== location.origin) return;

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(url, {
      method: "POST",
      headers: {
        [header]: token,
        "X-Requested-With": "XMLHttpRequest",
        "X-SPA": "true"
      },
      credentials: "same-origin",
    });

    if (!res.ok) throw new Error(`Fetch Failed: ${res.status}`);

    startResendTimer(RESEND_MS);
    startOrResumeCountDown();

    const result = await res.text();
    console.log(result);
  } catch (e) {
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }
}

function initIntercept() {
  console.log("Initializing Intercept..");

  cacheElements();

  if (
    !authForm ||
    !resend
    //!timer
    //!submit
  ) return;

  console.log(resend);

  startOrResumeCountDown();

  if (authForm) authForm.addEventListener("submit", (e) => handleInterceptForm(e));
  if (resend) resend.addEventListener("click", (e) => handleInterceptLink(e));

  window.addEventListener("storage", (e) => {
    if (e.key === KEY) startOrResumeCountDown();
  });
}

export { initIntercept };