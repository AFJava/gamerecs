import { search, searchDisplay, searchDebounce, filterDebounce } from "../service/search";

const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const expandedResultsDiv = document.querySelector(".search-results-expanded");
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