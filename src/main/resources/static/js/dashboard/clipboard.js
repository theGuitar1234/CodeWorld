// let 
//     isBound = false,
//     copyButton,
//     copySpan;

// function cacheElements() {
//     copyButton = document.getElementById("copy");
//     copySpan = document.getElementById("copy-status");
// }

async function handleCopyClipboard(e) {

    console.log("hello");

    const copyButton = document.getElementById("copy");
    const copySpan = document.getElementById("copy-status");
    const phoneInput = document.querySelector("a[contactPhone]");

    if (!copyButton && !copySpan && !phoneInput) return;

    const text = String(phoneInput.href).substring(4);

    const canWriteToClipboard = 
        typeof navigator !== "undefined" &&
        "clipboard" in navigator &&
        typeof navigator.clipboard.writeText === "function" &&
        window.isSecureContext;
    if (!canWriteToClipboard) {
        // copySpan.textContent = "Copy not supported. Press Ctrl/Cmd+C on the highlight text";
        alert("Copy not supported. Press Ctrl/Cmd+C on the highlight text");
        return;
    }
    try {
        await navigator.clipboard.writeText(text);
        // copySpan.textContent = "Copied to clipboard!";
        alert("Copied to clipboard!");
    } catch (e) {
        // copySpan.textContent = "Couldn't copy automatically. Press Ctrl/Cmd+C on the highlight text";
        alert("Couldn't copy automatically. Press Ctrl/Cmd+C on the highlight text");
    }
}

// function initClipBoard() {
//     if (isBound) return;
//     isBound = true;

//     cacheElements();

//     if (!copyButton || !copySpan) return;

//     document.addEventListener("click", handleCopyClipboard);
// }

export { handleCopyClipboard }