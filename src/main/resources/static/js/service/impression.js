export async function sendImpressionData(impressionGameIds) {
    const csrfHeader = document.getElementById("csrf-header").content;
    const csrfToken = document.getElementById("csrf-token").content;

    const response = await fetch(
        "/games/rec/impression",

        {
            method: "POST",
            
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                impressionGameIds: impressionGameIds
            }),

            keepalive: true
        }
    );
}