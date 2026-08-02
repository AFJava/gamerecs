import { searchDisplay, searchDebounce, filterDebounce, getResultsMap, appendAddedMessageOther } from "../service/search.js";
import { rate, add } from "../service/add.js";
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

    const gameDiv = event.target.closest(".search-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    const rating = event.target.elements.rating.value;
    
    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));
    
    add(gameDiv, igdbId, gameName, rating, game);
});

expandedResultsDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }

    const gameDiv = event.target.closest(".search-item-expanded");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    const rating = event.target.elements.rating.value;
    
    //Use expandedResultsMap instead when not on searchbar
    const game = expandedResultsMap.get(Number(igdbId));
    
    add(gameDiv, igdbId, gameName, rating, game);
});