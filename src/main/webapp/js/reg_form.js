const title = document.getElementById("auth-title");

const registrationForm = document.getElementById("reg-form");
const loginForm = document.getElementById("login-form");

const regUsername = document.getElementById("username");
const regEmail = document.getElementById("email");
const regPassword = document.getElementById("password");
const regPasswordSubmit = document.getElementById("password-submit");

const loginUsername = document.getElementById("login-username");
const loginPassword = document.getElementById("login-password");


function validateRegPassword() {
    const value = regPassword.value.trim();
    if (!value) return "Пароль не может быть пустым";
    return null;
}

function validateRegPasswordSubmit() {
    const value = regPasswordSubmit.value.trim();
    if (value !== regPassword.value.trim()) return "Пароли различаются!";
    return null;
}

function clearErrors() {
    document.querySelectorAll('.error-text').forEach(el => el.remove());
}

function showError(message, input) {
    const errorEl = document.createElement("div");
    errorEl.className = 'error-text';
    errorEl.textContent = message;
    errorEl.style.color = 'red';
    errorEl.style.fontSize = '14px';
    input.parentElement.appendChild(errorEl);
}

loginForm.addEventListener("submit", async function (event) {
    try {
        await Auth.login(
            loginUsername.value.trim(),
            loginPassword.value
        );

        location.href = 'index.html';

    } catch (error) {
        showError(error, loginUsername);
    }
});

registrationForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    clearErrors();

    const checks = [
        [regPassword, validateRegPassword],
        [regPasswordSubmit, validateRegPasswordSubmit],
    ];

    let isValid = true;
    for (const [input, validator] of checks) {
        const message = await validator();
        if (message) {
            showError(message, input);
            isValid = false;
        }
    }
    if (!isValid) return;

    const data = {
        regUsername: regUsername.value.trim(),
        regEmail: regEmail.value.trim(),
        regPassword: regPassword.value
    };
    try {
        await Auth.register(data.regUsername, data.regEmail, data.regPassword);

        await Auth.login(data.regUsername, data.regPassword);

        location.href = 'index.html';

    } catch (error) {
        showError(error, regUsername);
    }
})

function showForm(mode) {
    const isRegister = mode === "register";

    loginForm.hidden = isRegister;
    registrationForm.hidden = !isRegister;
    title.textContent = isRegister ? "Регистрация" : "Вход";
    document.title = title.textContent;

    clearErrors();
}

const showRegisterButton = document.getElementById("register-btn");
const showLoginButton = document.getElementById("login-btn")
showRegisterButton.addEventListener("click", function () {
    showForm("register");
});

showLoginButton.addEventListener("click", function () {
    showForm("login");
});


if (Auth.isLoggedIn()) {
    window.location.replace("index.html");
}