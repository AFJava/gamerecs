export async function sendImpressionData(impressionGameIds) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch(
        "/games/rec/impression",

        {
            method: "POST",
            
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: {
                impressionGameIds: JSON.stringify(impressionGameIds)
            }
        }
    );
}