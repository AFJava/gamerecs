import { getFavoritedGamesIgdbIds } from "./search.js";

export async function fav(gameDiv, igdbId, game) {
    const favoritedGamesIgdbIds = getFavoritedGamesIgdbIds();
    favoritedGamesIgdbIds.add(Number(igdbId));

    appendFavoritedConfirmationMessage(gameDiv, igdbId);
    sendFavRequest(igdbId, game);
}

export function appendFavoritedConfirmationMessage(gameDiv) {
    const actionDiv = gameDiv.querySelector(".game-action-container");
    const favButton = actionDiv.querySelector(".fav-button");

    favButton.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been favorited.</p>';
    
    actionDiv.appendChild(gameAddedMsgContainer);
}

export async function sendFavRequest(igdbId, game) {
    const csrfHeader = document.getElementById("csrf-header").content;
    const csrfToken = document.getElementById("csrf-token").content;
    
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