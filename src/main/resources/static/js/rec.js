const recDiv = document.querySelector(".rec-games");

if(recDiv !== null) {
    recDiv.addEventListener("click", (event) => {
        if (event.target.classList.contains("rate-button")) {
            rate(event);
        }
    });
}

const newRecButton = document.querySelector(".rec-button");

newRecButton.addEventListener("click", async () => {
    await rec();

    window.location.href = window.location.pathname + "/recommended?page=1";
});

async function rec() {
    const response = await fetch(
        "/games/rec"
    )
}