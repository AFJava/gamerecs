export function confirmRemove(gameDiv, location) {
    const igdbId = gameDiv.dataset.igdbId;
    const gameName = gameDiv.dataset.gameName;

    const makeInterface = prepareRemovalInterface(gameDiv.parentElement, igdbId);

    if(! makeInterface) {
        return;
    }

    //Build interface div
    const removalInterface = document.createElement("div");

    removalInterface.classList.add("removal-interface");
    removalInterface.dataset.igdbId = igdbId;

    removalInterface.innerHTML = `<p>Really remove ${gameName} from your ${location}?</p>
        <form method="post" class="removal-form">
            <button type="button" class="removal-button">No</button>
            <button type="submit" class="removal-button">Yes</button>
        </form>`;
    
    gameDiv.appendChild(removalInterface);
}

//Return whether to create/append interface
//Remove existing rating interface if it is in the same container AND not attached to the same game
function prepareRemovalInterface(containerDiv, igdbId) {
    const current = containerDiv.querySelector(".removal-interface");

    console.log(containerDiv);
    console.log(current);

    if (current !== null) {
        if(current.dataset.igdbId != String(igdbId)) {
            current.remove();
        }
        else {
            return false;
        }
    }

    return true;
}

export async function remove(gameDiv) {
    const igdbId = gameDiv.dataset.igdbId;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(
        `/games/remove/${igdbId}`, 
        {
            method: "DELETE",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            }
        }
    )
}

export async function abortRemove(interface) {
    interface.remove();
}