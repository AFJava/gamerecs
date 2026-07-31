document.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        fav(event)
    }
});

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

const recButtonContainer = document.getElementById("rec-button-container");
const newRecButton = recButtonContainer.querySelector(".rec-button");

if(newRecButton !== null) {
    newRecButton.addEventListener("click", async () => {
        await rec();

        window.location.href = window.location.pathname + "/recommended?page=1";
    });
}