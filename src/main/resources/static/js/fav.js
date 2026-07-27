document.addEventListener("click", (event) => {
    fav(event)
});

async function fav(event) {
    const favButton = event.target;
    const igdbId = favButton.dataset.igdbId;

    console.log(igdbId);

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(
        "/games/favorite",

        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                id: igdbId
            })
        }
    );
}