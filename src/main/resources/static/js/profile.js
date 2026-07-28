//Remove all messages displayed when no games are added, if any
function removeDefaultMessages() {
    const noGamesMsgs = document.querySelectorAll(".no-games-msg");

    if (noGamesMsgs !== null) {
        noGamesMsgs.forEach(msg => msg.remove());
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