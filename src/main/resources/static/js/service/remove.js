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
            <button type="button" class="removal-button-cancel">No</button>
            <button type="submit" class="removal-button-confirm">Yes</button>
        </form>`;
    
    gameDiv.appendChild(removalInterface);

    //This may have to move to pages
    removalInterface.querySelector(".removal-button-cancel").addEventListener("click", () => {
        abortRemove(removalInterface);
    });
}

//Consider making this a more general function to work with both rate, removal interfaces
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

export async function abortRemove(confirmInterface) {
    confirmInterface.remove();
}