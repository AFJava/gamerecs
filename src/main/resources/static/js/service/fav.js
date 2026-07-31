async function fav(event) {
    const gameDiv = event.target.closest(".search-item, .rec-item");
    const igdbId = gameDiv.dataset.igdbId;

    console.log(igdbId);

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    
    appendFavoritedConfirmationMessage(gameDiv, igdbId);

    if(document.getElementById("search-script")) {
        console.log("Search ver executed");
        
        sendFavRequestSearch(csrfHeader, csrfToken, igdbId);

        if(document.getElementById("profile-script") !== null) {
            renderFavorited(gameDiv);
        }
    }

    if(document.getElementById("rec-games")) {
        console.log("This one executed too");
        sendFavRequestRec(csrfHeader, csrfToken, igdbId);
    }
}

function appendFavoritedConfirmationMessage(gameDiv, igdbId) {
    const actionDiv = gameDiv.querySelector(".game-action-container");
    const favButton = actionDiv.querySelector(".fav-button");

    favButton.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been favorited.</p>';
    
    actionDiv.appendChild(gameAddedMsgContainer);
}

async function sendFavRequestSearch(csrfHeader, csrfToken, igdbId) {
    const game = getGame(igdbId);
    
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

    favoritedGamesIgdbIds.add(Number(igdbId));
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