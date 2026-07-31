export function setUpProfile(addedGamesContainer, recButtonContainer) {
    let recButton = recButtonContainer.querySelector(".rec-button");

    //If true, this is the first added game, so also remove all default messages
    if(recButton === null) {
        let noGamesMsg = addedGamesContainer.querySelector(".no-games-msg");
        noGamesMsg.remove();

        noGamesMsg = recButtonContainer.querySelector(".no-games-msg");
        noGamesMsg.remove();

        recButton = document.createElement("button");
        recButton.classList.add("rec-button");
        recButton.innerText = "Get New Recommendations";

        recButtonContainer.appendChild(recButton);

        //Also needs event listener upon first being added (otherwise handled in rec.js)
        recButton.addEventListener("click", async () => {
            await rec();

            window.location.href = window.location.pathname + "/recommended?page=1";
        });
    }
}

/* 
 * gameDiv - the .search-item div for the game containing all relevant fields
 * rating - if null, this is a favorited game
 */
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
        const profileCard = document.createElement("div");
        profileCard.classList.add("profile-card");
        
        profileCard.innerHTML = `<img src = ${imageSrc} class = "game-preview">
        <h2 class = "game-name">${gameName}</h2>
        <p class="game-rating">Rating: ${rating} / 10</p>`;

        profileGameDiv.appendChild(profileCard);
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
            <h2 class="game-name">${gameName}</h2>
            <div class="game-action-container">
                <button type="button" class="rate-button">Rate and add to profile</button>
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