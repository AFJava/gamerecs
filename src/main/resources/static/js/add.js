//If expandedResultsDiv exists, apply same listner to it
const expandedResultsDiv = document.querySelector(".search-results-expanded");

if(expandedResultsDiv !== null) {
    expandedResultsDiv.addEventListener("click", (event) => {
        if (event.target.classList.contains("rate-button")) {
            rate(event);
        }
    });
}

async function rate(event) {
    console.log("Button clicked");

    const rateButton = event.target;
    const gameName = rateButton.dataset.gameName;
    const igdbId = rateButton.dataset.igdbId;

    //Build interface div
    const rateInterface = document.createElement("div");

    console.log("div created");

    rateInterface.classList.add("rate");
    rateInterface.dataset.igdbId = igdbId;

    rateInterface.innerHTML = `<p>Rate ${gameName} and add it to your profile:</p>
        <form method="post" class="rate-form">
            <span><input type="number" name="rating" min="1" max="10"> / 10</span>
            <button type = "submit" class="submit-rating">Submit rating and add to profile</button>
        </form>`;

    //Append rating div
    const gameDiv = event.target.closest(".search-item, .rec-item");

    //Check if there is an existing rating interface
    const current = document.querySelector(".rate");

    //If not, append
    if (!current) {
        gameDiv.appendChild(rateInterface);
    } //If so, check if the current interface is for the same game; replace if not
    else if (current.dataset.igdbId != String(igdbId)) {
        current.remove();
        gameDiv.appendChild(rateInterface);
    }

    //Add event listener to form to handle submission
    const form = document.querySelector('.rate-form');

    form.addEventListener("submit", add);
}

async function add(event) {
    console.log("Submitted form");

    event.preventDefault();

    if(document.querySelector('script[src="profile.js"]') !== null) {
        removeMessages();
    }

    //Get CSRF
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    
    //Get data for db fields, get rateInterface + rateButton for deletion
    const rateInterface = event.target.closest(".rate");
    const igdbId = rateInterface.dataset.igdbId;

    const rateInput = document.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    if(document.querySelector('script[src="/js/search.js"]')) {
        sendAddRequestSearch(csrfHeader, csrfToken, igdbId, rating);
    }

    console.log("POST sent");

    //Deprecated id matching
    //window.userGamesIgdbIds.add(Number(igdbId));
    //console.log(window.userGamesIgdbIds);

    addConfirmationMesssages(rateInterface, igdbId);

    //If on profile, also render the game (or add button)
    if(document.querySelector('script[src="profile.js"]') !== null) {
        renderAdded(summaryDiv, igdbId);
    }
}

async function sendAddRequestSearch(csrfHeader, csrfToken, igdbId, rating) {
    const game = resultsMap.get(Number(igdbId))
    
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

    addedGamesIgdbIds.add(Number(igdbId));
}

function addConfirmationMesssages(rateInterface, igdbId) {
    //Replace rate button and rating interface with confirmation messages
    const rateButton = document.querySelector(`.rate-button[data-igdb-id = "${igdbId}"]`);
    rateButton.remove();
    rateInterface.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been added to your profile.</p>';

    //Append to div in place of button
    const actionDiv = document.querySelector(`.search-summary[data-igdb-id = "${igdbId}"], .rec-action-container[data-igdb-id = "${igdbId}"]`);
    actionDiv.appendChild(gameAddedMsgContainer);

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    const gameName = rateButton.dataset.gameName;
    confirmation.innerHTML = `<p>${gameName} was added to your profile.</p>`;

    //Append confirmation message to correct gameDiv
    const gameDiv = actionDiv.closest(".search-item, .rec-item");

    gameDiv.appendChild(confirmation);
}