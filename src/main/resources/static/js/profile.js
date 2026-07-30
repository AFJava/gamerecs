function setUpProfile(addedGamesContainer, recButtonContainer) {
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
function renderAdded(gameDiv, rating) {
    const idgbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const gamePreview = gameDiv.querySelector(`.game-preview-search`);
    console.log(gamePreview);

    const imageId = gamePreview.dataset.imageId;
    const imageSrc = "https://images.igdb.com/igdb/image/upload/t_1080p/" + imageId + ".jpg";
    
    const isFavorite = rating === null;

    let profileGameDivContainer;
    let profileGameDiv;
    
    
    //Check whether to display added game
    if(! isFavorite) {
        profileGameDivContainer = document.getElementById("added-games-container");
        profileGameDiv = profileGameDivContainer.querySelector(".added-games");
    }
    else {
        profileGameDivContainer = document.getElementById("favorited-games-container");
        profileGameDiv = profileGameDivContainer.querySelector(".favorited-games");
    }
    
    let expandAdded = profileGameDivContainer.querySelector(".expanded-nav-container");

    //Each gameDiv includes one info message
    if (profileGameDiv.childElementCount < 6) {
        //Use JS to display newly added game without refresh (use Thymeleaf for games previously added)
        const profileCard = document.createElement("div");
        profileCard.classList.add("profile-card");
        
        if(! isFavorite) {
            profileCard.innerHTML = `<img src = ${imageSrc} class = "game-preview">
            <h2 class = "game-name">${gameName}</h2>
            <p class="game-rating">Rating: ${rating} / 10</p>`;
        } else {
            profileCard.innerHTML = `<img src = ${imageSrc} class = "game-preview">
            <h2 class = "game-name">${gameName}</h2>
            <p class="game-rating">Placeholder</p>`;
        }

        profileGameDiv.appendChild(profileCard);
    } //Otherwise add the new button (if not already rendered)
    else if (expandAdded === null) {
        expandAdded = document.createElement("div");
        expandAdded.classList.add("expanded-nav-container");

        if(! isFavorite) {
            expandAdded.innerHTML = '<a href="profile/added?page=1" class="button">All added games</a>';
        } else {
            expandAdded.innerHTML = `<a href="profile/favorites?page=1" class="button">All favorited games</a>`
        }

        profileGameDivContainer.appendChild(expandAdded);
    }
}