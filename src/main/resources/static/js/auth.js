document.addEventListener("DOMContentLoaded", () => {
    console.log("DOM loaded");

    const form = document.querySelector(".signup form");

    console.log(form);

    form.addEventListener("submit", signup);

    console.log("listener activated");
});

async function signup(e) {
    console.log("signup called");

    e.preventDefault();

    console.log("Default behavior prevented")

    const email = document.getElementById("email").value;

    const password = document.getElementById("password").value;

    const response = await fetch(
        "http://localhost:8080/auth/signup",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
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