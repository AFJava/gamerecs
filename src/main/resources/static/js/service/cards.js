export function setUpProfile(recButtonContainer) {
    let recButton = recButtonContainer.querySelector(".rec-button");

    if(recButton !== null) {
        return;
    }

    recButton = document.createElement("button");
    recButton.classList.add("rec-button");
    recButton.innerText = "Get New Recommendations";

    const currentRecsButton = recButtonContainer.querySelector(".button");

    if(currentRecsButton !== null) {
        recButtonContainer.insertBefore(recButton, currentRecsButton);
        return;
    }

    recButtonContainer.appendChild(recButton);
}

export function resetProfile(addedGamesContainer, addedGamesDiv, favoritedGamesContainer, favoritedGamesDiv, recButtonContainer) {
    //Container always has header, games list div (2), div always has info message (1) if there are no games added/favorited at all
    if((addedGamesContainer.childElementCount + addedGamesDiv.childElementCount != 3) ||
        (favoritedGamesContainer.childElementCount + favoritedGamesDiv.childElementCount != 3)) {
        return;
    }

    let recButton = recButtonContainer.querySelector(".rec-button");
    recButton.remove();
}

export function renderAdded(gameDiv, rating) {
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const gamePreview = gameDiv.querySelector(`.game-preview-search, .game-preview`);
    console.log(gamePreview);

    const imageId = gamePreview.dataset.imageId;
    const imageSrc = "https://images.igdb.com/igdb/image/upload/t_1080p/" + imageId + ".jpg";

    let profileGameDivContainer = document.getElementById("added-games-container");
    let profileGameDiv = profileGameDivContainer.querySelector(".added-games");
    
    let expandAdded = profileGameDivContainer.querySelector(".expanded-nav-container");
    
    //Check whether to display added game; note that gameDiv includes one info message
    if (profileGameDiv.childElementCount < 6) {
        //Use JS to display newly added game without refresh (use Thymeleaf for games previously added)
        const addedItem = document.createElement("div");
        addedItem.classList.add("added-item");
        addedItem.dataset.igdbId = igdbId;
        addedItem.dataset.gameName = gameName;
        
        addedItem.innerHTML = `<div class="profile-card">
            <img src = ${imageSrc} class = "game-preview">
            <div class="game-info-container">
                <h2 class = "game-name">${gameName}</h2>
                <p class="game-rating">Rating: ${rating} / 10</p>
            </div>
            <div class="game-action-container-profile">
                <button type="button" class="removal-button">Remove game from profile</button>
            </div>
        </div>`;

        profileGameDiv.appendChild(addedItem);
    } //Otherwise add the new button (if not already rendered)
    else if (expandAdded === null) {
        expandAdded = document.createElement("div");
        expandAdded.classList.add("expanded-nav-container");

        expandAdded.innerHTML = '<a href="profile/added?page=1" class="button">All added games</a>';

        profileGameDivContainer.appendChild(expandAdded);
    }
}

export function renderFavorited(gameDiv) {
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const gamePreview = gameDiv.querySelector(`.game-preview-search`);
    console.log(gamePreview);

    const imageId = gamePreview.dataset.imageId;
    const imageSrc = "https://images.igdb.com/igdb/image/upload/t_1080p/" + imageId + ".jpg";

    let profileGameDivContainer = document.getElementById("favorited-games-container");;
    let profileGameDiv = profileGameDivContainer.querySelector(".favorited-games");
    
    let expandAdded = profileGameDivContainer.querySelector(".expanded-nav-container");

    //Check whether to display added game; note that gameDiv includes one info message
    if (profileGameDiv.childElementCount < 6) {
        //Use JS to display newly added game without refresh (use Thymeleaf for games previously added)
        const favItem = document.createElement("div");
        favItem.classList.add("fav-item");
        favItem.dataset.igdbId = igdbId;
        favItem.dataset.gameName = gameName;

        favItem.innerHTML = `<div class="profile-card">
            <img class="game-preview" src="${imageSrc}" data-image-id="${imageId}">
            <div class="game-info-container">
                <h2 class="game-name">${gameName}</h2>
            </div>
            <div class="game-action-container-profile">
                <button type="button" class="rate-button">Rate and add to profile</button>
                <button type="button" class="removal-button">Remove game from favorites</button>
            </div>
        </div>`

        profileGameDiv.appendChild(favItem);
    } //Otherwise add the new button (if not already rendered)
    else if (expandAdded === null) {
        expandAdded = document.createElement("div");
        expandAdded.classList.add("expanded-nav-container");

        expandAdded.innerHTML = `<a href="profile/favorites?page=1" class="button">All favorited games</a>`

        profileGameDivContainer.appendChild(expandAdded);
    }
}