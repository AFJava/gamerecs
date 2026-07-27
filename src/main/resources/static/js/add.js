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

    //Add event listener to form to handle submission
    const form = document.querySelector('.rate-form');

    form.addEventListener("submit", add);
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

async function add(event) {
    console.log("Submitted form");

    event.preventDefault();

    //Get CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    
    //Get data for db fields, get rateInterface + rateButton for deletion
    const rateInterface = event.target.closest(".rate");
    const igdbId = rateInterface.dataset.igdbId;

    const rateInput = document.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    if(document.querySelector('script[src="/js/search.js"]')) {
        sendAddRequestSearch();
    }

    console.log("POST sent");

    //Deprecated id matching
    //window.userGamesIgdbIds.add(Number(igdbId));
    //console.log(window.userGamesIgdbIds);

    //Replace rate button and rating interface with confirmation messages
    const rateButton = document.querySelector(`.rate-button[data-igdb-id = "${igdbId}"]`);
    rateButton.remove();
    rateInterface.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been added to your profile.</p>';

    //Append to summary div in place of button
    const summaryDiv = document.querySelector(`.search-summary[data-igdb-id = "${igdbId}"], .rec-action-container[data-igdb-id = "${igdbId}"]`);
    summaryDiv.appendChild(gameAddedMsgContainer);

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    const gameName = rateButton.dataset.gameName;
    confirmation.innerHTML = `<p>${gameName} was added to your profile.</p>`;

    //Append confirmation messages to correct gameDiv
    const gameDiv = summaryDiv.closest(".search-item, .rec-item");

    gameDiv.appendChild(confirmation);

    //If on profile, also render the game (or add button)
    if(document.querySelector('script[src="profile.js"]') !== null) {
        renderAdded(summaryDiv, igdbId);
    }
}

async function sendAddRequestSearch() {
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