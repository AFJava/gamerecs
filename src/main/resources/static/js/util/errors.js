export function displayError(container, message, title = "") {
    const errMsg = document.createElement("p");
    errMsg.classList.add("no-games-msg");
    errMsg.innerText = message;
    errMsg.title = title;

    container.appendChild(errMsg);
}