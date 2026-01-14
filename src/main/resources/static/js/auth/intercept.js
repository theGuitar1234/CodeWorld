let forgotPasswordForm;

function cacheElements() {
  forgotPasswordForm = document.getElementById("forgot-password-form");
}

async function handleIntercept(e) {
  e.preventDefault();
  console.log("I see you tried to submit this form but I intercepted hahahaha");

  const url = new URL(forgotPasswordForm.getAttribute("action"), location.origin);

  if (url.origin !== location.origin) return;

  console.log(forgotPasswordForm.querySelector("input").value);
  url.searchParams.set("email", forgotPasswordForm.querySelector("input").value);

  try {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    const res = await fetch(url, {
      method: "POST",
      headers: {
        [header]: token,
        "X-Requested-With": "fetch",
      },
      credentials: "same-origin",
    });

    if (!res.ok) throw new Error(`Fetch Failed: ${res.status}`);

    const result = await res.text();
    console.log(result);
  } catch (e) {
    alert(`Something went wrong, try again: ${e}`);
    console.error(e);
  }
}

function initIntercept() {
  console.log("Initializing Intercept");

  cacheElements();

  if (!forgotPasswordForm) return;

  console.log(forgotPasswordForm.getAttribute("action"));

  forgotPasswordForm.addEventListener("submit", (e) => handleIntercept(e));
}

export { initIntercept };
