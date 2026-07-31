//Ensure that DOM Content loads before adding event listeners; otherwise event listeners may not function properly
//const signupForm = document.querySelector(".signup form");

//signupForm.addEventListener("submit", signup);

//Note these are HTML elements for real-time feedback
const passwordInput = document.getElementById("password");
const retypeInput = document.getElementById("retype");
const signupErr = document.getElementById("signuperr");

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

    //Note that these are values as they will be sent to Spring
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