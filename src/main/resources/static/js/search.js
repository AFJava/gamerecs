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
resultsDiv.addEventListener("click", (event) => {
    if(event.target.classList.contains("rate-button")) {
        console.log("Button clicked");

        const rateButton = event.target;
        const gameName = rateButton.dataset.gameName;
        const gameId = rateButton.dataset.gameId;

        //Build interface div
        const rateInterface = document.createElement("div");

        console.log("div created");

        rateInterface.classList.add("rate");
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
    }
})

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

        gameDiv.innerHTML = `<img src = ${game.background_image} class = "game-preview">
            <h2 class = "game-name">${game.name}</h2>
            <button type="submit" class = "rate-button" data-game-id = "${game.id}" data-game-name = "${game.name}">Rate and add to profile</button>`;
        
        resultsDiv.appendChild(gameDiv);
    });
}