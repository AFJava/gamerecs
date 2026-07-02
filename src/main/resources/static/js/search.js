//console.log("JS loaded");

//TODO: Move searchbar back on z-axis when clicking away, then re-prioritize when clicking back on searchbar
//Also remove search results instantly when empty

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const filterObscureButton = document.getElementById("filter-obscure");
const resultsMap = new Map();

let debounceTimeout;
let debounceTime = 1000; //in ms (CURRENTLY SET TO 3 SECONDS FOR DEVELOPMENT)

filterObscureButton.addEventListener("change", () => {
    clearTimeout(debounceTimeout);

    search();
})

searchbar.addEventListener("input", () => {
    //console.log("event triggered");

    //Only do an API search if user stops typing for 1 second
    clearTimeout(debounceTimeout);

    debounceTimeout = setTimeout(() => {
        search();
    }, debounceTime); 
});

//If user clicked off search results, remove from display; if clicked on, re-enable display
document.addEventListener("click", (event) => {
    //Avoid changing active status when clicking on filter button (reruns search anyways) or empty top-left grid cell
    if( !(resultsDiv.contains(event.target) || searchbar.contains(event.target)) ) {
        resultsDiv.classList.remove("active");
    } 
    else if(searchbar.contains(event.target)) {
        resultsDiv.classList.add("active");
    }
});

async function search() {
    console.log("search began");

    resultsMap.clear();

    const searchContent = searchbar.value;
    const filterObscureChecked = filterObscureButton.checked;

    //Clear search results
    resultsDiv.replaceChildren();
    resultsDiv.classList.add("active");

    //console.log(searchContent);

    //DO NOT search if under 3 alphanumeric characters are given
    if(searchContent.trim().length < 3) {
        return;
    }

    //Build URI depending on filter status
    const searchURI = `/api/games/search?q=${encodeURIComponent(searchContent)}&filterObscure=${filterObscureChecked}`;

    console.log("URI built");

    //Response receives a List<IgdbGameDto> object which converts to JSON containing a list of search content along with search metadata
    const response = await fetch(
        searchURI,
    );

    //console.log("request sent");

    const games = await response.json()
    console.log(games); //DEBUG

    games.forEach(game => {
        const gameDiv = document.createElement("\cdiv");

        const igdbId = game.id;

        gameDiv.classList.add("search-item");
        gameDiv.dataset.igdbId = `${igdbId}`; //Same id used for button

        const searchSummary = document.createElement("div");
        searchSummary.classList.add("search-summary");
        searchSummary.dataset.igdbId = `${igdbId}`;
        resultsMap.set(Number(igdbId), game);

        //Create IGDB image link from image_id
        const image_id = game.cover.image_id;
        const imageURL = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + image_id + ".jpg"

        //Select confirmation message or rate & add button depending on whether game has been added
        const actionHTML = window.userGamesIgdbIds.has(Number(igdbId))
            ? `<span class="game-added-msg-container"><p class = "game-added-msg">This game has already been added to your profile.</p></span>`
            : `<button type="button" class="rate-button" data-igdb-id = "${igdbId}" data-game-name = "${game.name}">Rate and add to profile</button>`
        
        searchSummary.innerHTML = `<img src = ${imageURL} class = "game-preview-search">
            <h2 class = "game-name">${game.name}</h2>
            ${actionHTML}`;

        gameDiv.appendChild(searchSummary);
        resultsDiv.appendChild(gameDiv);
    });
}