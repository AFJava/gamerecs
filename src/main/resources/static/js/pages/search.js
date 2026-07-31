import { search, searchDisplay, searchDebounce, filterDebounce, getResultsMap, getAddedGamesIgdbIds } from "../service/search.js";
import { rate, sendAddRequestSearch, appendAddedConfirmationMessages } from "../service/add.js";
import { fav } from "../service/fav.js";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const expandedResultsDiv = document.querySelector(".search-results-expanded");
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
        fav(event);
    }
});

expandedResultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        fav(event);
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
        
    //Get data for db fields, get rateInterface + rateButton for deletion
    const gameDiv = event.target.closest(".search-item, .rec-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const rateInput = gameDiv.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId))

    sendAddRequestSearch(csrfHeader, csrfToken, igdbId, rating, game);
    appendAddedConfirmationMessages(gameDiv, igdbId, gameName);

    const addedGamesIgdbIds = getAddedGamesIgdbIds();
    addedGamesIgdbIds.add(Number(igdbId));
});