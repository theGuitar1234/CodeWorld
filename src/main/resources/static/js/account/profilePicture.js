let input, form, isBound = false;

function cacheElements() {
  input = document.getElementById("profile-picture");
  form = document.getElementById("profile-picture-form");
}

function initProfilePicture() {

  console.log("Initializing Profile Picture");

  if (isBound) return;
  isBound = false;

  cacheElements();

  if (!input || !form) return;

  input.addEventListener("input", () => {
    form.submit();
  });
}

export { initProfilePicture };
