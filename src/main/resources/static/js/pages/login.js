//const signupForm = document.querySelector(".signup form");

//signupForm.addEventListener("submit", signup);

const passwordInput = document.getElementById("password");
const retypeInput = document.getElementById("retype");
const loginDiv = document.getElementById("login");
const signupDiv = document.getElementById("signup");
const signupErr = signupDiv.querySelector(".auth-error");

passwordInput.addEventListener("input", checkPasswords);
retypeInput.addEventListener("input", checkPasswords);

function checkPasswords() {
    if(retypeInput.value === "") {
        signupErr.textContent = "";
    } else if(passwordInput.value !== retypeInput.value) {
        signupErr.textContent = "Passwords do not match";
    } else {
        signupErr.textContent = "";
    }
}

/*
async function signup(e) {
    e.preventDefault(); //Default behavior refreshes which cancels all operations below

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const retype = document.getElementById("retype").value;

    //Take CSRF token from HTML
    const csrfToken = document.querySelector('meta[name = "_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name = "_csrf_header"]').content;

    const response = await fetch(
        "/auth/signup",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken //Supply CSRF token with header
            },

            body: JSON.stringify({
                email: email,
                password: password,
                retype: retype
            })
        }
    )
} */