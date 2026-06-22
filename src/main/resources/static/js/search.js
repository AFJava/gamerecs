const searchbar = document.querySelector(".search input");
const resultsDiv = document.querySelector(".search-results")

let debounceTimeout;

searchbar.addEventListener("input", () => {
    //Only do an API search if user stops typing for 1 second
    clearTimeout(debounceTimeout);

    debounceTimeout = setTimeout(() => {
        search();
    }, 3000); //CURRENTLY SET TO 3 SECONDS FOR DEVELOPMENT
});

//Check whether a button was clicked within the search results div
resultsDiv.addEventListener("click", rate);

async function rate(event) {
    if(event.target.classList.contains("rate-button")) {
        console.log("Button clicked");

        const rateButton = event.target;
        const gameName = rateButton.dataset.gameName;
        const gameId = rateButton.dataset.gameId;

        //Build interface div
        const rateInterface = document.createElement("div");

        console.log("div created");

        rateInterface.classList.add("rate");
        rateInterface.dataset.gameId = gameId;

        rateInterface.innerHTML = `<p>Rate ${gameName} and add it to your profile:</p>
        <form method="post" class="rate-form">
            <span><input type="number" name="rating" min="1" max="10"> / 10</span>
            <button type = "submit">Submit rating and add to profile</button>
        </form>`;

        //Get gameDiv by gameId and append rating div
        const gameDiv = event.target.closest(".search-item");

        //Check if there is an existing rating interface
        const current = document.querySelector(".rate");

        //If not, append
        if(!current) {
            gameDiv.appendChild(rateInterface);
        } //If so, check if the current interface is for the same game; replace if not
        else if(current.dataset.gameId != String(gameId)) {
            current.remove();
            gameDiv.appendChild(rateInterface);
        }

        //Add event listener to form to handle submission
        const form = document.querySelector('.rate-form');

        form.addEventListener("submit", add);
    }
}

async function add(event) {
    console.log("Submitted form");

    event.preventDefault();

    console.log("Navigation halted");

    //Get data for db fields, get rateInterface + rateButton for deletion
    const rateInterface = event.target.closest(".rate");
    const gameId = rateInterface.dataset.gameId;

    const rateInput = document.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    const rateButton = document.querySelector(`.rate-button[data-game-id = "${gameId}"]`);
    const gameName = rateButton.dataset.gameName;

    const gamePreview = document.querySelector(".game-preview-search");
    const imageSrc = gamePreview.src;

    //Get CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(
        "/games/add",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                rawgId: gameId,
                name: gameName,
                rating: rating,
                imageSrc: imageSrc
            })
        }
    )

    console.log("POST sent");

    //Replace rate button and rating interface with confirmation messages
    
    rateButton.remove();
    rateInterface.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been added to your profile.</p>'; //TODO: Make this message appear in place of button afterwards, if game is already in db

    //Append to search-summary in place of button
    const searchSummary = document.querySelector(`.search-summary[data-game-id = "${gameId}"]`);
    searchSummary.appendChild(gameAddedMsgContainer);

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    confirmation.innerHTML = `<p>${gameName} was added to your profile.</p>`;

    //Append confirmation messages to correct gameDiv
    const gameDiv = document.querySelector(`.search-item[data-game-id = "${gameId}"]`);

    gameDiv.appendChild(confirmation);

    //Use JS to display newly added game without refresh (use Thymeleaf for games previously added)
    const profileCard = document.createElement("div");
    profileCard.classList.add("profile-card");
    profileCard.innerHTML = `<img src = ${imageSrc} class = "game-preview">
            <h2 class = "game-name">${gameName}</h2>
            <p class="game-rating">Rating: ${rating} / 10</p>`;
    
    const profileGameDiv = document.querySelector(".added-games");
    profileGameDiv.appendChild(profileCard);
}

async function search() {
    const searchContent = searchbar.value;

    //Clear search results
    resultsDiv.replaceChildren();

    //DO NOT search if under 3 alphanumeric characters are given
    if(searchContent.trim().length < 3) {
        return;
    }

    //Response receives a RawgSearchResponse object which converts to JSON containing a list of search content along with search metadata
    const response = await fetch(
        "/api/games/search?q=" + encodeURIComponent(searchContent),
    );

    const games = await response.json()
    console.log(games); //DEBUG

    games.results.forEach(game => {
        const gameDiv = document.createElement("div");
        gameDiv.classList.add("search-item");
        gameDiv.dataset.gameId = `${game.id}`; //Same id used for button

        const searchSummary = document.createElement("div");
        searchSummary.classList.add("search-summary");
        searchSummary.dataset.gameId = `${game.id}`;

        //Select confirmation message or rate & add button depending on whether game has been added
        const actionHTML = window.userGamesRawgIds.has(Number(game.id))
            ? `<span class="game-added-msg-container"><p class = "game-added-msg">This game has already been added to your profile.</p></span>`
            : `<button type="button" class="rate-button" data-game-id = "${game.id}" data-game-name = "${game.name}">Rate and add to profile</button>`
        
        searchSummary.innerHTML = `<img src = ${game.background_image} class = "game-preview-search">
            <h2 class = "game-name">${game.name}</h2>
            ${actionHTML}`;

        gameDiv.appendChild(searchSummary);
        resultsDiv.appendChild(gameDiv);
    });
}