const searchbar = document.querySelector(".searchbar");
const resultsDiv = document.querySelector(".search-results");
const filterObscureButton = document.getElementById("filter-obscure");
const searchNavDiv = document.querySelector(".page-nav");
const resultsList = document.querySelector(".results-list");

let totalPages = 0;

let debounceTimeout;
let debounceTime = 1000; //in ms (SET TO 1 SECOND FOR DEVELOPMENT)

filterObscureButton.addEventListener("change", () => {
    clearTimeout(debounceTimeout);

    search(1);
})

searchbar.addEventListener("input", () => {
    //console.log("event triggered");

    //Clear results immediately if empty
    if(searchbar.value.length == 0) {
        resultsList.replaceChildren();
        searchNavDiv.replaceChildren();
    }

    //Only do an API search if user stops typing for 1 second
    clearTimeout(debounceTimeout);

    debounceTimeout = setTimeout(() => {
        search(1);
    }, debounceTime); 
});

//If user clicked off search results, remove from display; if clicked on, re-enable display
document.addEventListener("click", (event) => {
    //Avoid changing active status when clicking on filter button (reruns search anyways) or empty top-left grid cell
    if( !(resultsDiv.contains(event.target) || searchbar.contains(event.target)) ) {
        resultsDiv.classList.remove("active");
    } 
    else if(searchbar.contains(event.target)) {
        resultsDiv.classList.add("active");
    }
});

resultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});

//If expandedResultsDiv exists, apply same listner to it
const expandedResultsDiv = document.querySelector(".search-results-expanded");

expandedResultsDiv.addEventListener("click", (event) => {
    if (event.target.classList.contains("rate-button")) {
        rate(event);
    }
});