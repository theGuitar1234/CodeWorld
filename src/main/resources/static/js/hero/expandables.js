let isBound = false;
let notification, profile;

function cacheElements() {
  notification = document.getElementById("notification-expandable");
  profile = document.getElementById("profile-expandable");
}

function handleExpandables(e) {
  const isNotificationOpen = notification.getAttribute("open") != null;
  const isProfileOpen = profile.getAttribute("open") != null;

  console.log(isNotificationOpen, isProfileOpen);

  if (e.target.closest("#notification-expandable") && 
      isProfileOpen
  ) {
    profile.removeAttribute("open");
  }

  if (e.target.closest("#profile-expandable") &&
      isNotificationOpen
  ) {
    notification.removeAttribute("open");
  }
}

function initExpandables() {
  console.log("Initializing Expandables...");
  if (isBound) return;
  isBound = true;

  cacheElements();
  if (!notification || !profile) return;

  document.addEventListener("click", handleExpandables);
}

export { initExpandables };
