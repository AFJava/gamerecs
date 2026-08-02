import { search, searchDisplay, searchDebounce, filterDebounce, getResultsMap, getAddedGamesIgdbIds, getFavoritedGamesIgdbIds } from "../service/search.js";
import { rate, sendAddRequest, appendAddedConfirmationMessage, appendRateConfirmationMessage } from "../service/add.js";
import { fav } from "../service/fav.js";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const expandedResultsDiv = document.getElementById("search-results-expanded");
const filterObscureButton = document.getElementById("filter-obscure");

filterObscureButton.addEventListener("change", filterDebounce);

searchbar.addEventListener("input", searchDebounce);

document.addEventListener("click", searchDisplay);

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

expandedResultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        const gameDiv = event.target.closest(".search-item");
        const igdbId = gameDiv.dataset.igdbId;

        const resultsMap = getResultsMap();
        const game = resultsMap.get(Number(igdbId));

        fav(gameDiv, igdbId, game);
    }
});

expandedResultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        const gameDiv = event.target.closest(".search-item-expanded");
        const igdbId = gameDiv.dataset.igdbId;

        //Use expandedResultsMap instead when not on searchbar
        const game = expandedResultsMap.get(Number(igdbId));

        fav(gameDiv, igdbId, game);
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
});

expandedResultsDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }

    //Get CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        
    const gameDiv = event.target.closest(".search-item-expanded");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const rateInput = gameDiv.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    //Use expandedResultsMap instead when not on searchbar
    const game = expandedResultsMap.get(Number(igdbId));
    
    const addedGamesIgdbIds = getAddedGamesIgdbIds();
    addedGamesIgdbIds.add(Number(igdbId));

    sendAddRequest(csrfHeader, csrfToken, igdbId, rating, game);
    appendAddedConfirmationMessage(gameDiv);
    appendRateConfirmationMessage(gameDiv, gameName);
});