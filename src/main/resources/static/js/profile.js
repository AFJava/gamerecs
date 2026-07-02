//Check whether a button was clicked within the search results div
resultsDiv.addEventListener("click", rate);

async function rate(event) {
    if(event.target.classList.contains("rate-button")) {
        console.log("Button clicked");

        const rateButton = event.target;
        const gameName = rateButton.dataset.gameName;
        const igdbId = rateButton.dataset.igdbId;

        //Build interface div
        const rateInterface = document.createElement("div");

        console.log("div created");

        rateInterface.classList.add("rate");
        rateInterface.dataset.igdbId = igdbId;

        rateInterface.innerHTML = `<p>Rate ${gameName} and add it to your profile:</p>
        <form method="post" class="rate-form">
            <span><input type="number" name="rating" min="1" max="10"> / 10</span>
            <button type = "submit">Submit rating and add to profile</button>
        </form>`;

        //Append rating div
        const gameDiv = event.target.closest(".search-item");

        //Check if there is an existing rating interface
        const current = document.querySelector(".rate");

        //If not, append
        if(!current) {
            gameDiv.appendChild(rateInterface);
        } //If so, check if the current interface is for the same game; replace if not
        else if(current.dataset.igdbId != String(igdbId)) {
            current.remove();
            gameDiv.appendChild(rateInterface);
        }

        //Add event listener to form to handle submission
        const form = document.querySelector('.rate-form');

        form.addEventListener("submit", add);
    }
}

async function add(event) {
    console.log("Submitted form");

    event.preventDefault();

    console.log("Navigation halted");

    //Get data for db fields, get rateInterface + rateButton for deletion
    const rateInterface = event.target.closest(".rate");
    const igdbId = rateInterface.dataset.igdbId;

    const rateInput = document.querySelector('input[name="rating"]');
    const rating = rateInput.value;

    const game = resultsMap.get(Number(igdbId));

    //Get CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(
        "/games/add",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                igdbId: igdbId,
                rating: rating,
                game: game
            })
        }
    )

    console.log("POST sent");

    //Replace rate button and rating interface with confirmation messages
    const rateButton = document.querySelector(`.rate-button[data-igdb-id = "${igdbId}"]`);
    rateButton.remove();
    rateInterface.remove();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = '<p class = "game-added-msg">This game has already been added to your profile.</p>';

    //Append to search-summary in place of button
    const searchSummary = document.querySelector(`.search-summary[data-igdb-id = "${igdbId}"]`);
    searchSummary.appendChild(gameAddedMsgContainer);

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    const gameName = rateButton.dataset.gameName;
    confirmation.innerHTML = `<p>${gameName} was added to your profile.</p>`;

    const gamePreview = document.querySelector(".game-preview-search");
    const imageSrc = gamePreview.src;

    //Append confirmation messages to correct gameDiv
    const gameDiv = document.querySelector(`.search-item[data-igdb-id = "${igdbId}"]`);

    gameDiv.appendChild(confirmation);

    //Use JS to display newly added game without refresh (use Thymeleaf for games previously added)
    const profileCard = document.createElement("div");
    profileCard.classList.add("profile-card");
    profileCard.innerHTML = `<img src = ${imageSrc} class = "game-preview">
            <h2 class = "game-name">${gameName}</h2>
            <p class="game-rating">Rating: ${rating} / 10</p>`;
    
    const profileGameDiv = document.querySelector(".added-games");
    profileGameDiv.appendChild(profileCard);
}