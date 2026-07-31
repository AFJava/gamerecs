//sendFavRequestRec(csrfHeader, csrfToken, igdbId);

export function appendFavoritedConfirmationMessage(gameDiv, igdbId) {
    const actionDiv = gameDiv.querySelector(".game-action-container");
    const favButton = actionDiv.querySelector(".fav-button");

    favButton.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been favorited.</p>';
    
    actionDiv.appendChild(gameAddedMsgContainer);
}

export async function sendFavRequestSearch(csrfHeader, csrfToken, igdbId, game) {
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
}

export async function sendFavRequestRec(csrfHeader, csrfToken, igdbId) {
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