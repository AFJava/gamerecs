const searchbar = document.querySelector(".search input");
const resultsDiv = document.querySelector(".search_results")

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
    if(event.target.classList.contains("rate_button")) {
        console.log("Button clicked");

        const rateButton = event.target;
        const gameName = rateButton.dataset.gameName;
        const gameId = rateButton.dataset.gameId;

        const rateInterface = document.createElement("div");

        console.log("div created");

        rateInterface.classList.add("rate");
        rateInterface.innerHTML = `<p>Rate ${gameName} and add it to your profile:</p>`;

    }
})

async function search() {
    const searchContent = searchbar.value;

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
        gameDiv.classList.add("search_item");

        gameDiv.innerHTML = `<img src = ${game.background_image} class = "game_preview">
            <p class = "game_name">${game.name}</p>
            <button class = "rate_button" data-game-id = "${game.id}" data-game-name = "${game.name}">Rate and add to profile</button>`;
        
        resultsDiv.appendChild(gameDiv);
    });
}