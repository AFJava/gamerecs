document.addEventListener("DOMContentLoaded", () => {
    const signUpButton = document.getElementById("signup");

    signUpButton.addEventListener("click", signup);
});

async function signup() {
    console.log("signup called");

    event.preventDefault();

    const email = document.getElementById("email");

    const password = document.getElementById("password");

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