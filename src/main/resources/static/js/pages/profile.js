import { searchDisplay, searchDebounce, filterDebounce, getResultsMap, appendAddedMessageOther } from "../service/search.js";
import { rate, add, appendAddedConfirmationMessage } from "../service/add.js";
import { setUpProfile, renderAdded, renderFavorited } from "../service/cards.js";
import { fav } from "../service/fav.js";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const filterObscureButton = document.getElementById("filter-obscure");
const favoritedGamesDiv = document.getElementById("favorited-games-container").querySelector(".favorited-games");

filterObscureButton.addEventListener("change", filterDebounce);

searchbar.addEventListener("input", searchDebounce);

document.addEventListener("click", searchDisplay);

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        const gameDiv = event.target.closest(".search-item");
        rate(gameDiv);
    }
});

favoritedGamesDiv.addEventListener("click", (event) => {
    console.log(favoritedGamesDiv);

    if (event.target.classList.contains("rate-button")) {
        const gameDiv = event.target.closest(".fav-item");
        rate(gameDiv);
    }
});

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        const gameDiv = event.target.closest(".search-item");
        const igdbId = gameDiv.dataset.igdbId;
        
        const resultsMap = getResultsMap();
        const game = resultsMap.get(Number(igdbId));
    
        fav(gameDiv, igdbId, game);
        renderFavorited(gameDiv);
    }
});

resultsDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }
        
    const gameDiv = event.target.closest(".search-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    const rating = event.target.elements.rating.value;
    
    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));
    
    add(gameDiv, igdbId, gameName, rating, game);

    const addedGamesContainer = document.getElementById("added-games-container");
    const recButtonContainer = document.getElementById("rec-button-container");

    setUpProfile(addedGamesContainer, recButtonContainer);
    renderAdded(gameDiv, rating);

    //If a favorited game was just added from the searchbar, check if it is displayed on the profile; if so, append message there as well
    appendAddedMessageOther(favoritedGamesDiv, "fav-item", igdbId)
});

favoritedGamesDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }

    const gameDiv = event.target.closest(".fav-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    const rating = event.target.elements.rating.value;
    
    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));
    
    add(gameDiv, igdbId, gameName, rating, game);

    const addedGamesContainer = document.getElementById("added-games-container");
    const recButtonContainer = document.getElementById("rec-button-container");

    setUpProfile(addedGamesContainer, recButtonContainer);
    renderAdded(gameDiv, rating);

    //If a favorited game was just added from favorites list, check if it is displayed on the searchbar; if so, append message there as well
    appendAddedMessageOther(resultsDiv, "search-item", igdbId)
});

const newRecButton = document.getElementById("rec-button-container").querySelector(".rec-button");

if(newRecButton !== null) {
    newRecButton.addEventListener("click", async () => {
        await rec();

        window.location.href = window.location.pathname + "/recommended?page=1";
    });
}