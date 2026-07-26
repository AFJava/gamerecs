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