export async function rec() {
    const csrfHeader = document.getElementById("csrf-header").content;
    const csrfToken = document.getElementById("csrf-token").content;

    const response = await fetch(
        "/games/rec",

        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            }
        }
    )

    return response;
}