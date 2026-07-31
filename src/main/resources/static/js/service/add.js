export async function rate(event) {
    console.log("Button clicked");
    
    //Check whether to build interface div
    const gameDiv = event.target.closest(".search-item, .rec-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    
    const makeInterface = prepareRatingInterface(igdbId);

    if(! makeInterface) {
        return;
    }

    //Build interface div
    const rateInterface = document.createElement("div");

    rateInterface.classList.add("rate");
    rateInterface.dataset.igdbId = igdbId;

    rateInterface.innerHTML = `<p>Rate ${gameName} and add it to your profile:</p>
        <form method="post" class="rate-form">
            <span><input type="number" name="rating" min="1" max="10"> / 10</span>
            <button type = "submit" class="submit-rating">Submit rating and add to profile</button>
        </form>`;
    
    gameDiv.appendChild(rateInterface);
}

//Return whether to create/append interface
//Remove existing rating interface IF it is not the same as the current
function prepareRatingInterface(igdbId) {
    const current = document.querySelector(".rate");

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

//sendAddRequestRec(csrfHeader, csrfToken, igdbId, rating);

export async function sendAddRequest(csrfHeader, csrfToken, igdbId, rating, game) {
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
export function appendAddedConfirmationMessages(gameDiv, igdbId, gameName) {
    const actionDiv = gameDiv.querySelector(`.game-action-container`);
    const rateInterface = gameDiv.querySelector(".rate");
    
    //When added, also remove any option to favorite
    actionDiv.replaceChildren();
    rateInterface.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been added to your profile.</p>';
    
    actionDiv.appendChild(gameAddedMsgContainer);

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    confirmation.innerHTML = `<p>${gameName} was added to your profile.</p>`;

    gameDiv.appendChild(confirmation);
}