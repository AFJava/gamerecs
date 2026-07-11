async function rec() {
    //Backend handles retrieval of added games and all rec logic
    const response = await fetch(
        "/games/rec"
    )
    
    const recs = await response.json()

    console.log(recs);

    //Display recommendations
}

window.rec = rec;