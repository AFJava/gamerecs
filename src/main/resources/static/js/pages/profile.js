import { searchDisplay, searchDebounce, filterDebounce, getResultsMap, getAddedGamesIgdbIds, getFavoritedGamesIgdbIds, resetGameActionsOther, appendAddedMessageOther, removeOther } from "../service/search.js";
import { rate, add, sendAddRequest, appendAddedConfirmationMessage } from "../service/add.js";
import { setUpProfile, renderAdded, renderFavorited, resetProfile, renderAddedNav, renderFavoritedNav } from "../service/cards.js";
import { fav } from "../service/fav.js";
import { rec } from "../service/rec.js";
import { confirmRemove, remove } from "../service/remove.js";
import { displayError } from "../util/errors.js";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const filterObscureButton = document.getElementById("filter-obscure");

const addedGamesContainer = document.getElementById("added-games-container");
const addedGamesDiv = addedGamesContainer.querySelector(".added-games");
const favoritedGamesContainer = document.getElementById("favorited-games-container");
const favoritedGamesDiv = favoritedGamesContainer.querySelector(".favorited-games");
const recButtonContainer = document.getElementById("rec-button-container");

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

favoritedGamesDiv.addEventListener("click", (event) => {
    //console.log(favoritedGamesDiv);

    if (!event.target.classList.contains("rate-button")) {
        return;
    }

    const gameDiv = event.target.closest(".fav-item");
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

    if (favoritedGamesDiv.childElementCount < 6) {
        renderFavorited(favoritedGamesDiv, gameDiv);
    }
    else if (favoritedGamesContainer.querySelector(".expanded-nav-container") === null) {
        renderFavoritedNav(favoritedGamesContainer)
    }

    setUpProfile(recButtonContainer);
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

    setUpProfile(recButtonContainer);

    if (addedGamesDiv.childElementCount < 6) {
        renderAdded(addedGamesDiv, gameDiv, rating);
    }
    else if (addedGamesContainer.querySelector(".expanded-nav-container") === null) {
        renderAddedNav(addedGamesContainer)
    }

    //If a favorited game was just added from the searchbar, check if it is displayed on the profile; if so, append message there as well
    removeOther(favoritedGamesDiv, "fav-item", igdbId);
});

favoritedGamesDiv.addEventListener("submit", (event) => {
    event.preventDefault();

    if (!event.target.matches(".rate-form")) {
        return;
    }

    const gameDiv = event.target.closest(".fav-item");
    const igdbId = gameDiv.dataset.igdbId;
    const rating = event.target.elements.rating.value;

    const resultsMap = getResultsMap();
    const game = resultsMap.get(Number(igdbId));

    //No need to do extra rendering, just remove gameDiv and send request
    sendAddRequest(igdbId, rating, game);

    setUpProfile(recButtonContainer);
    if (addedGamesDiv.childElementCount < 6) {
        renderAdded(addedGamesDiv, gameDiv, rating);
    }
    else if (addedGamesContainer.querySelector(".expanded-nav-container") === null) {
        renderAddedNav(addedGamesContainer)
    }

    //If a favorited game was just added from favorites list, check if it is displayed on the searchbar; if so, append message there as well
    appendAddedMessageOther(resultsDiv, "search-item", igdbId);
    gameDiv.remove();
});

addedGamesDiv.addEventListener("click", (event) => {
    if (!event.target.classList.contains("removal-button")) {
        return;
    }

    confirmRemove(event.target.closest(".added-item"), "profile");
});

favoritedGamesDiv.addEventListener("click", (event) => {
    if (!event.target.classList.contains("removal-button")) {
        return;
    }

    confirmRemove(event.target.closest(".fav-item"), "favorites");
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
    resetProfile(addedGamesContainer, addedGamesDiv, favoritedGamesContainer, favoritedGamesDiv, recButtonContainer)
});

favoritedGamesDiv.addEventListener("submit", (event) => {
    event.preventDefault();

    if (!event.target.matches(".removal-form")) {
        return;
    }

    const gameDiv = event.target.closest(".fav-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    remove(gameDiv, igdbId, gameName, "favorites");

    const favoritedGamesIgdbIds = getFavoritedGamesIgdbIds();
    favoritedGamesIgdbIds.delete(Number(igdbId));

    resetGameActionsOther(resultsDiv, "search-item", igdbId);
    resetProfile(addedGamesContainer, addedGamesDiv, favoritedGamesContainer, favoritedGamesDiv, recButtonContainer)
});

/* Maybe put cancel remove here
addedGamesDiv.addEventListener("click", (event) => {

});

favoritedGamesDiv.addEventListener("click", (event) => {

});
*/

recButtonContainer.addEventListener("click", async (event) => {
    if (!event.target.classList.contains("rec-button")) {
        return;
    }

    try{
        const response = await rec();
        
        if(response.status === 429) {
                displayError(
                    recButtonContainer, 
                    "Recommendations are temporarily rate limited. Please try again momentarily.",
                    "Game data is provided by IGDB, which limits this application to 4 requests per second. Please try again in a moment."
                );

                return;
            }

            if(! response.ok) {
                displayError(
                    recButtonContainer, 
                    "Something went wrong while searching. Please try again.",
                    "Unknown error occurred."
                );

                return;
            }
        }
    catch (error) {
        displayError(
            recButtonContainer, 
            "Something went wrong while searching. Please try again.",
            "Unknown error occurred."
        );

        return;
    }

    window.location.href = window.location.pathname + "/recommended?page=1";
});