import { searchDisplay, searchDebounce, filterDebounce, getResultsMap, getAddedGamesIgdbIds, resetGameActionsOther } from "../service/search.js";
import { rate, add } from "../service/add.js";
import { fav } from "../service/fav.js";
import { confirmRemove, remove } from "../service/remove.js";
import { renderAdded, renderPageNav, removePageNav } from "../service/cards.js";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const filterObscureButton = document.getElementById("filter-obscure");
const addedGamesDiv = document.getElementById("added-games-container").querySelector(".added-games-expanded");
const pageNavDiv = document.getElementById("page-nav-expanded");
let currentPages = pageNavDiv.childElementCount - 1;

const pageSize = 10;

filterObscureButton.addEventListener("change", filterDebounce);

searchbar.addEventListener("input", searchDebounce);

document.addEventListener("click", searchDisplay);

resultsDiv.addEventListener("click", (event) => {
    if (!event.target.classList.contains("rate-button")) {
        return;
    }

    const gameDiv = event.target.closest(".search-item");
    rate(gameDiv);
});

resultsDiv.addEventListener("click", (event) => {
    if (!event.target.classList.contains("fav-button")) {
        return;
    }

    const gameDiv = event.target.closest(".search-item");
    const igdbId = gameDiv.dataset.igdbId;

    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));

    fav(gameDiv, igdbId, game);
});

resultsDiv.addEventListener("submit", (event) => {
    event.preventDefault();

    if (!event.target.matches(".rate-form")) {
        return;
    }

    const gameDiv = event.target.closest(".search-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    const rating = event.target.elements.rating.value;

    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));

    add(gameDiv, igdbId, gameName, rating, game);

    console.log("Added game; total elements is " + pageNavDiv.dataset.totalElements);
    if (addedGamesDiv.childElementCount < pageSize + 1) {
        renderAdded(addedGamesDiv, gameDiv, rating);
    }
    if (Number(pageNavDiv.dataset.totalElements) % pageSize === 0) {
        console.log(pageNavDiv.dataset.totalElements);
        currentPages++;
        renderPageNav(pageNavDiv, window.location.pathname, currentPages);
    }

    pageNavDiv.dataset.totalElements = Number(pageNavDiv.dataset.totalElements) + 1;
});

addedGamesDiv.addEventListener("click", (event) => {
    if (!event.target.classList.contains("removal-button")) {
        return;
    }

    confirmRemove(event.target.closest(".added-item"), "profile");
});

addedGamesDiv.addEventListener("submit", (event) => {
    event.preventDefault();

    if (!event.target.matches(".removal-form")) {
        return;
    }

    const gameDiv = event.target.closest(".added-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    remove(gameDiv, igdbId, gameName, "profile");

    const addedGamesIgdbIds = getAddedGamesIgdbIds();
    addedGamesIgdbIds.delete(Number(igdbId));

    resetGameActionsOther(resultsDiv, "search-item", igdbId);

    console.log("Removed");

    if ((Number(pageNavDiv.dataset.totalElements) - 1) % pageSize === 0) {
        console.log("Removed page nav");
        currentPages--;
        removePageNav(pageNavDiv);
    }

    pageNavDiv.dataset.totalElements = Number(pageNavDiv.dataset.totalElements) - 1;
});