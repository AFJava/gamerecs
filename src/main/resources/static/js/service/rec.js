export async function rec() {
    const csrfHeader = document.getElementById("csrf-header").content;
    const csrfToken = document.getElementById("csrf-token").content;

    const response = await fetch(
        "/games/rec",

        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            }
        }
    )

    return response;
}

export function displayError(container, message, title = "") {
    const errMsg = document.createElement("p");
    errMsg.classList.add("no-games-msg");
    errMsg.innerText = message;
    errMsg.title = title;

    container.appendChild(errMsg);
}