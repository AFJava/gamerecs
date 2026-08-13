import { rate, add } from "../service/add.js";
import { fav } from "../service/fav.js";
import { rec } from "../service/rec.js"

const recButtonContainer = document.getElementById("rec-button-container");
const newRecButton = recButtonContainer.querySelector(".rec-button");
const recDiv = document.getElementById("rec-games");
const impressionGameIds = [];

const observer = new IntersectionObserver(
    (entries) => {
        entries.forEach(entry => {
            if(entry.isIntersecting) {
                const igdbId = entry.target.dataset.igdbId;
                impressionGameIds.push(igdbId);

                console.log("Game with igdbId " + igdbId + " added to list");
                observer.unobserve(entry.target);
            }
        })
    },
    {
        threshold: 0.5
    }
);

recDiv.querySelectorAll(".game-display").forEach((display) => {
    observer.observe(display);
});

recDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        const gameDiv = event.target.closest(".rec-item");
        rate(gameDiv);
    }
});

recDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("fav-button")) {
        const gameDiv = event.target.closest(".rec-item");
        const igdbId = gameDiv.dataset.igdbId;
        
        fav(gameDiv, igdbId, null);
    }
});

recDiv.addEventListener("submit", (event) => {
    event.preventDefault();
    
    if(! event.target.matches(".rate-form")) {
        return;
    }
    
    const gameDiv = event.target.closest(".rec-item");
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;
    const rating = event.target.elements.rating.value;
    
    add(gameDiv, igdbId, gameName, rating, null);
});

newRecButton.addEventListener("click", async () => {
    await rec();

    //refresh page
    window.location.href = window.location.pathname;
});