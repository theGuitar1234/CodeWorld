import { handleContact } from "./contact.js";
import { handleCopyClipboard } from "./clipboard.js";

let isBound = false;

async function handleInitContactMain(e) {
    const contactLink = e.target.closest("a[contactLink]");

    if (contactLink) handleContact(e)

    const copyClipBoard = e.target.closest("#copy");

    if (copyClipBoard) handleCopyClipboard(e)
}

function initContactMain() {
    console.log("Initializing Contact");

    if (isBound) return;
    isBound = true;
    
    document.addEventListener("click", async (e) => handleInitContactMain(e));
}

export { initContactMain }