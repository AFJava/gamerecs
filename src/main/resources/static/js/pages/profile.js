import { search, searchDisplay, searchDebounce, filterDebounce, getResultsMap, getAddedGamesIgdbIds, getFavoritedGamesIgdbIds } from "../service/search.js";
import { rate, sendAddRequest, appendAddedConfirmationMessage, appendRateConfirmationMessage } from "../service/add.js";
import { setUpProfile, renderAdded, renderFavorited } from "../service/cards.js";
import { appendFavoritedConfirmationMessage, sendFavRequest } from "../service/fav.js";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const expandedResultsDiv = document.querySelector(".search-results-expanded");
const filterObscureButton = document.getElementById("filter-obscure");
const favoritedGamesDiv = document.getElementById("favorited-games-container").querySelector(".favorited-games");

filterObscureButton.addEventListener("change", filterDebounce);

searchbar.addEventListener("input", searchDebounce);

document.addEventListener("click", searchDisplay);

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

favoritedGamesDiv.addEventListener("click", (event) => {
    console.log(favoritedGamesDiv);

    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        const gameDiv = event.target.closest(".search-item, .rec-item");
        const igdbId = gameDiv.dataset.igdbId;

        console.log(igdbId);

        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        const resultsMap = getResultsMap();
        const game = resultsMap.get(Number(igdbId));

        const favoritedGamesIgdbIds = getFavoritedGamesIgdbIds();
        favoritedGamesIgdbIds.add(Number(igdbId));

        appendFavoritedConfirmationMessage(gameDiv, igdbId);
        sendFavRequest(csrfHeader, csrfToken, igdbId, game);
        renderFavorited(gameDiv);
    }
});

resultsDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }

    //Get CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        
    const gameDiv = event.target.closest(".search-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const rateInput = gameDiv.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));
    
    const addedGamesIgdbIds = getAddedGamesIgdbIds();
    addedGamesIgdbIds.add(Number(igdbId));

    sendAddRequest(csrfHeader, csrfToken, igdbId, rating, game);
    appendAddedConfirmationMessage(gameDiv);
    appendRateConfirmationMessage(gameDiv, gameName);

    const addedGamesContainer = document.getElementById("added-games-container");
    const recButtonContainer = document.getElementById("rec-button-container");

    setUpProfile(addedGamesContainer, recButtonContainer);
    renderAdded(gameDiv, rating);

    //If a favorited game was just added from the searchbar, check if it is displayed on the profile; if so, append message there as well
    const favoritedGamesIgdbIds = getFavoritedGamesIgdbIds();
    
    if(favoritedGamesIgdbIds.has(Number(igdbId))) {
        const favoritedGameDiv = favoritedGamesDiv.querySelector(`.fav-item[data-igdb-id="${igdbId}"]`);

        if(favoritedGameDiv !== null) {
            appendAddedConfirmationMessage(favoritedGameDiv);
        }
    }
});

favoritedGamesDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }

    //Get CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        
    const gameDiv = event.target.closest(".fav-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const rateInput = gameDiv.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));
    
    const addedGamesIgdbIds = getAddedGamesIgdbIds();
    addedGamesIgdbIds.add(Number(igdbId));

    sendAddRequest(csrfHeader, csrfToken, igdbId, rating, game);
    appendAddedConfirmationMessage(gameDiv);
    appendRateConfirmationMessage(gameDiv, gameName);

    const addedGamesContainer = document.getElementById("added-games-container");
    const recButtonContainer = document.getElementById("rec-button-container");

    setUpProfile(addedGamesContainer, recButtonContainer);
    renderAdded(gameDiv, rating);

    //If a favorited game was just added from favorites list, check if it is displayed on the searchbar; if so, append message there as well
    const favoritedGamesIgdbIds = getFavoritedGamesIgdbIds();
    
    if(favoritedGamesIgdbIds.has(Number(igdbId))) {
        const resultsGameDiv = resultsDiv.querySelector(`.search-item[data-igdb-id="${igdbId}"]`);

        if(resultsGameDiv !== null) {
            appendAddedConfirmationMessage(resultsGameDiv);
        }
    }
});

const newRecButton = document.getElementById("rec-button-container").querySelector(".rec-button");

if(newRecButton !== null) {
    newRecButton.addEventListener("click", async () => {
        await rec();

        window.location.href = window.location.pathname + "/recommended?page=1";
    });
}