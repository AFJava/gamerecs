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
    //console.log(games); //DEBUG

    games.results.forEach(game => {
        const gameDiv = document.createElement("div");
        gameDiv.classList.add("search_item");

        gameDiv.innerHTML = `<img src = ${game.background_image} class = "game_preview">
            <p class = "game_name">${game.name}</p>
            <a href = "/rate" class = "game_rate">Rate and add to profile</a>`;
        
        resultsDiv.appendChild(gameDiv);
    });
}