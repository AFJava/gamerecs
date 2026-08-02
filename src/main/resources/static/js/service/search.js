import { appendAddedConfirmationMessage } from "./add.js";

//TODO: Stop search in progress if another search begins

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const filterObscureButton = document.getElementById("filter-obscure");
const searchNavDiv = document.querySelector(".page-nav");
const resultsList = document.querySelector(".results-list");
const resultsMap = new Map();

let debounceTimeout;
let debounceTime = 1000; //in ms (SET TO 1 SECOND FOR DEVELOPMENT)

let addedGamesIgdbIds = new Set();
let favoritedGamesIgdbIds = new Set();
let lastSearchQuery = "";
let lastSearchResult = [];
let lastSearchFilterChecked;

export function filterDebounce() {
    clearTimeout(debounceTimeout);

    search(1);
}

export function searchDebounce() {
    //console.log("event triggered");

    //Clear results immediately if empty
    if(searchbar.value.length == 0) {
        resultsList.replaceChildren();
        searchNavDiv.replaceChildren();
    }

    //Only do an API search if user stops typing for 1 second
    clearTimeout(debounceTimeout);

    debounceTimeout = setTimeout(() => {
        search(1);
    }, debounceTime);
}

export function searchDisplay(event) {
    //If user clicked off search results, remove from display; if clicked on, re-enable display
    //Avoid changing active status when clicking on filter button (reruns search anyways) or empty top-left grid cell
    if( !(resultsDiv.contains(event.target) || searchbar.contains(event.target) || event.target.matches(".fav-button")) ) {
        console.log(resultsDiv.contains(event.target));
        console.log(searchbar.contains(event.target));
        console.log(resultsDiv.isConnected);
        resultsDiv.classList.remove("active");
    } 
    else if(searchbar.contains(event.target)) {
        resultsDiv.classList.add("active");
    }
}

export async function search(page) {
    console.log("search began");

    resultsMap.clear();

    const searchContent = searchbar.value;
    const filterObscureChecked = filterObscureButton.checked;

    //Clear search results
    resultsList.replaceChildren();
    searchNavDiv.replaceChildren();
    resultsDiv.classList.add("active");

    //console.log(searchContent);

    let games = [];

    //If searchContent not same as cached query, perform new search and re-cache
    if(searchContent !== lastSearchQuery || filterObscureChecked !== lastSearchFilterChecked) {
        //DO NOT search if under 2 alphanumeric characters are given
        if(searchContent.trim().length < 2) {
            const noGamesMessage = document.createElement("p");
            noGamesMessage.classList.add("no-games-msg");
            noGamesMessage.textContent = "No search results found. Please enter a longer query. ";

            resultsList.appendChild(noGamesMessage);

            return;
        }

        //Build URI depending on filter status
        const searchURI = `/games/search?q=${encodeURIComponent(searchContent)}&filterObscure=${filterObscureChecked}`;

        console.log("URI built");

        //Response receives a List<IgdbGameDto> object which converts to JSON containing a list of search content along with search metadata
        const response = await fetch(
            searchURI,
        );

        const responseData = await response.json();

        //console.log("request sent");

        //Cache query, results and store displayed page in games
        addedGamesIgdbIds = new Set(responseData.addedGamesIgdbIds);
        favoritedGamesIgdbIds = new Set(responseData.favoritedGamesIgdbIds);
        lastSearchQuery = searchContent;
        lastSearchResult = responseData.games;
        lastSearchFilterChecked = filterObscureChecked;
    }
    
    //From cached result, retrieve page for display 
    for(let i = 5 * (page - 1); i < Math.min(5 * page, lastSearchResult.length); i++) {
        games.push(lastSearchResult[i]);
    }

    //If results are completely empty display message
    if(games.length == 0) {
        const noGamesMessage = document.createElement("p");
        noGamesMessage.classList.add("no-games-msg");
        noGamesMessage.textContent = "No search results for \"" + searchContent + "\". ";

        resultsList.appendChild(noGamesMessage);

        return;
    }

    console.log(games); //DEBUG

    renderPage(games);
    renderPageNav(page, searchContent, filterObscureChecked);
}

function renderPage(games) {
    games.forEach(game => {
        const gameDiv = document.createElement("div");

        const igdbId = game.id;

        gameDiv.classList.add("search-item");
        gameDiv.dataset.igdbId = `${igdbId}`; //Same id used for button
        gameDiv.dataset.gameName = `${game.name}`;

        const searchSummary = document.createElement("div");
        searchSummary.classList.add("search-summary");
        searchSummary.dataset.igdbId = `${igdbId}`;
        resultsMap.set(Number(igdbId), game);

        //Create IGDB image link from image_id
        let imageId = 0;
        let imageURL;

        if(game.cover !== null) {
            imageId = game.cover.image_id;
            imageURL = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + imageId + ".jpg";
        } else {
            imageURL = "/assets/Image_not_found.png";
        }
        
        //Select confirmation message or rate & add button depending on whether game has been added
        const actionDiv = document.createElement("div");
        actionDiv.classList.add("game-action-container");
        
        let addedHTML;
        let favoriteHTML; 
        
        if(addedGamesIgdbIds.has(Number(igdbId))) {
            addedHTML = `<span class="game-added-msg-container"><p class = "game-added-msg">This game has already been added to your profile.</p></span>`
            actionDiv.innerHTML = `${addedHTML}`;
        } else {
            addedHTML = `<button type="button" class="rate-button" data-igdb-id = "${igdbId}" >Rate and add to profile</button>`
            
            const favoriteHTML = favoritedGamesIgdbIds.has(Number(igdbId)) 
                ? `<span class="game-added-msg-container"><p class = "game-added-msg">This game has already been favorited.</p></span>`
                : `<button type="button" class="fav-button" data-igdb-id = "${igdbId}" >Add to favorites</button>`;
            
            actionDiv.innerHTML = `${addedHTML}
                ${favoriteHTML}`;
        }
        
        searchSummary.innerHTML = `<img data-image-id=${imageId} src = ${imageURL} class = "game-preview-search">
            <h2 class = "game-name">${game.name}</h2>`;

        searchSummary.appendChild(actionDiv);
        gameDiv.appendChild(searchSummary);
        resultsList.appendChild(gameDiv);
    });
}

function renderPageNav(page, searchContent, filterObscureChecked) {
    let pages = Math.ceil(lastSearchResult.length / 5);
    let hasMoreResults = pages > 5;
    pages = Math.min(pages, 5);
    
    let searchNavItem = document.createElement("p");
    searchNavItem.classList.add("page-indicator");
    searchNavItem.textContent = "Page: ";
    searchNavDiv.appendChild(searchNavItem);

    for(let i = 1; i <= pages; i++) {
        if(i === page) {
            searchNavItem = document.createElement("span");
            searchNavItem.classList.add("page-nav-current");
        }
        else {
            searchNavItem = document.createElement("button");
            searchNavItem.classList.add("page-nav-item-search");

            searchNavItem.addEventListener("click", () => {
                setTimeout(() => search(i), 0);
            });
        }

        searchNavItem.textContent = i;
        searchNavDiv.appendChild(searchNavItem);
    }

    //console.log(lastSearchResult.length);
    //console.log(hasMoreResults);

    if(hasMoreResults) {
        searchNavItem = document.createElement("span");
        searchNavItem.innerHTML = `<a class="button" href="/search?page=1&query=${encodeURIComponent(searchContent)}&filter-obscure=${filterObscureChecked}">More Results</a>`
        searchNavDiv.appendChild(searchNavItem);
    }
}

export function appendAddedMessageOther(otherDiv, otherItemName, igdbId) {
    const resultsGameDiv = otherDiv.querySelector(`.${otherItemName}[data-igdb-id="${igdbId}"]`);

    if(resultsGameDiv !== null) {
        appendAddedConfirmationMessage(resultsGameDiv);
    }
}

export function getResultsMap() {
    return resultsMap;
}

export function getAddedGamesIgdbIds() {
    return addedGamesIgdbIds;
}

export function getFavoritedGamesIgdbIds() {
    return favoritedGamesIgdbIds;
}

function getGame(igdbId) {
    let game = resultsMap.get(Number(igdbId))

    if(game === undefined) {
        //console.log("Game not found; searching cached results")
        game = expandedResultsMap.get(Number(igdbId));
    }

    return game;
}