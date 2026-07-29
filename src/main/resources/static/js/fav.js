document.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        fav(event)
    }
});

async function fav(event) {
    const favButton = event.target;
    const igdbId = favButton.dataset.igdbId;

    console.log(igdbId);

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    if(document.querySelector('script[src="/js/search.js"]')) {
        console.log("Search ver executed");
        sendFavRequestSearch(csrfHeader, csrfToken, igdbId);
    }

    if(document.querySelector(".rec-games")) {
        console.log("This one executed too");
        sendFavRequestRec(csrfHeader, csrfToken, igdbId);
    }
}

async function sendFavRequestSearch(csrfHeader, csrfToken, igdbId) {
    const game = resultsMap.get(Number(igdbId))
    
    const response = await fetch(
        "/games/favorite",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                igdbId: igdbId,
                game: game
            })
        }
    )

    //favoritedGamesIgdbIds.add(Number(igdbId));
}

async function sendFavRequestRec(csrfHeader, csrfToken, igdbId) {
    const response = await fetch(
        "/games/favorite",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                igdbId: igdbId,
                game: null
            })
        }
    )
}