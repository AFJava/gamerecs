const recDiv = document.getElementById("rec-games");

recDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

const newRecButton = recButtonContainer.querySelector(".rec-button");

newRecButton.addEventListener("click", async () => {
    await rec();

    window.location.href = window.location.pathname + "/recommended?page=1";
});