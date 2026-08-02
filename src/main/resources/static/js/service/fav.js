import { getFavoritedGamesIgdbIds } from "./search.js";

export async function fav(gameDiv, igdbId, game) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const favoritedGamesIgdbIds = getFavoritedGamesIgdbIds();
    favoritedGamesIgdbIds.add(Number(igdbId));

    appendFavoritedConfirmationMessage(gameDiv, igdbId);
    sendFavRequest(csrfHeader, csrfToken, igdbId, game);
}

export function appendFavoritedConfirmationMessage(gameDiv, igdbId) {
    const actionDiv = gameDiv.querySelector(".game-action-container");
    const favButton = actionDiv.querySelector(".fav-button");

    favButton.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been favorited.</p>';
    
    actionDiv.appendChild(gameAddedMsgContainer);
}

export async function sendFavRequest(csrfHeader, csrfToken, igdbId, game) {
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