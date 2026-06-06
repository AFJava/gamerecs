const searchbar = document.querySelector(".search input");

searchbar.addEventListener("input", search);

async function search() {
    const searchContent = searchbar.value;

    const response = await fetch(
        "/api/games/search?q=" + encodeURIComponent(searchContent),
    );

    
}


