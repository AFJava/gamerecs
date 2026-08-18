import { getAddedGamesIgdbIds } from "./search.js";

export function rate(gameDiv) {
    console.log("Button clicked");
    
    //Check whether to build interface div
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    
    const makeInterface = prepareRatingInterface(gameDiv.parentElement, igdbId);

    if(! makeInterface) {
        return;
    }

    //Build interface div
    const rateInterface = document.createElement("div");

    rateInterface.classList.add("rate");
    rateInterface.dataset.igdbId = igdbId;

    rateInterface.innerHTML = `<p>Rate ${gameName} and add it to your profile:</p>
        <form method="post" class="rate-form">
            <span><input type="number" name="rating" min="0" max="10" step="0.1"> / 10</span>
            <button type = "submit" class="submit-rating">Submit rating and add to profile</button>
        </form>`;
    
    gameDiv.appendChild(rateInterface);
}

//Return whether to create/append interface
//Remove existing rating interface if it is in the same container AND not attached to the same game
function prepareRatingInterface(containerDiv, igdbId) {
    const current = containerDiv.querySelector(".rate");

    console.log(containerDiv);
    console.log(current);

    if (current !== null) {
        if(current.dataset.igdbId != String(igdbId)) {
            current.remove();
        }
        else {
            return false;
        }
    }

    return true;
}

export async function add(gameDiv, igdbId, gameName, rating, game) {
    const addedGamesIgdbIds = getAddedGamesIgdbIds();
    addedGamesIgdbIds.add(Number(igdbId));

    sendAddRequest(igdbId, rating, game);
    appendAddedConfirmationMessage(gameDiv);
    appendRateConfirmationMessage(gameDiv, gameName);
}

export async function sendAddRequest(igdbId, rating, game) {
    const csrfHeader = document.getElementById("csrf-header").content;
    const csrfToken = document.getElementById("csrf-token").content;
    
    const response = await fetch(
        "/games/add",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                igdbId: igdbId,
                rating: rating,
                game: game
            })
        }
    )
}

//Replace rate button, interface with confirmation messages
export function appendAddedConfirmationMessage(gameDiv) {
    const actionDiv = gameDiv.querySelector(`.game-action-container`);
    
    //When added, also remove any option to favorite
    actionDiv.replaceChildren();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been added to your profile.</p>';
    
    actionDiv.appendChild(gameAddedMsgContainer);
}

export function appendRateConfirmationMessage(gameDiv, gameName) {
    const rateInterface = gameDiv.querySelector(".rate");
    rateInterface.remove();

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    confirmation.innerHTML = `<p>${gameName} was added to your profile.</p>`;

    gameDiv.appendChild(confirmation);
}