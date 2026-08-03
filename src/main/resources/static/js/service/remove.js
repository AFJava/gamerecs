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

export async function remove(gameDiv, igdbId, gameName, location) {
    sendRemoveRequest(igdbId);
    
    appendRemovedConfirmationMessage(gameDiv, location)
    appendRemovedInterfaceConfirmationMessage(gameDiv, gameName, location)
}

async function sendRemoveRequest(igdbId) {
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
    );
}

async function abortRemove(confirmInterface) {
    confirmInterface.remove();
}

//Replace rate button, interface with confirmation messages
function appendRemovedConfirmationMessage(gameDiv, location) {
    const actionDiv = gameDiv.querySelector(`.game-action-container-profile`);
    
    //When added, also remove any option to favorite
    actionDiv.replaceChildren();

    const gameAddedMsgContainer = document.createElement("span");
    gameAddedMsgContainer.classList.add("game-added-msg-container");

    gameAddedMsgContainer.innerHTML = `<p class = "game-added-msg">This game has already been removed from your ${location}.</p>`;
    
    actionDiv.appendChild(gameAddedMsgContainer);
}

function appendRemovedInterfaceConfirmationMessage(gameDiv, gameName, location) {
    const removalInterface = gameDiv.querySelector(".removal-interface");
    removalInterface.remove();

    const confirmation = document.createElement("div");
    confirmation.classList.add("confirmation");

    confirmation.innerHTML = `<p>${gameName} was removed from your ${location}.</p>`;

    gameDiv.appendChild(confirmation);
}