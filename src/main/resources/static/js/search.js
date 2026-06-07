const searchbar = document.querySelector(".search input");

searchbar.addEventListener("input", search);

async function search() {
    const searchContent = searchbar.value;

    //Response receives a RawgSearchResponse object containing a list of search content along with search metadata
    const response = await fetch(
        "/api/games/search?q=" + encodeURIComponent(searchContent),
    );


    const games = await response.json()
    console.log(games);
}


