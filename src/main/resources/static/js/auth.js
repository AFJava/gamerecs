document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".signup form");

    form.addEventListener("submit", signup);
});

async function signup(e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const csrfToken = document.querySelector('meta[name = "_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name = "_csrf_header"]').content;

    const response = await fetch(
        "/auth/signup",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },

            body: JSON.stringify({
                email: email,
                password: password
            })
        }
    )

    const parseResponse = await response.text();
    
    document.getElementById("signupmsg").textContent = parseResponse;
}