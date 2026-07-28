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
    }
}

function renderAdded(summaryDiv, igdbId, gameName, rating) {
    const gamePreview = summaryDiv.querySelector(`.game-preview-search`);
    console.log(gamePreview);

    const imageId = gamePreview.dataset.imageId;
    const imageSrc = "https://images.igdb.com/igdb/image/upload/t_1080p/" + imageId + ".jpg";

    //Check whether to display added game 
    const profileGameDivContainer = document.querySelector(".added-games-container");

    const profileGameDiv = profileGameDivContainer.querySelector(".added-games");
    let expandAdded = profileGameDivContainer.querySelector(".added-nav-container");

    if (profileGameDiv.childElementCount < 5) {
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
        expandAdded.classList.add("added-nav-container");

        expandAdded.innerHTML = '<a href="profile/added?page=1" class="button">All added games</a>';

        profileGameDivContainer.appendChild(expandAdded);
    }
}