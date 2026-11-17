const Auth = (function () {
    const ACCESS_KEY = "accessToken";
    const REFRESH_KEY = "refreshToken";
    const AUTH_URL = "api/v1/auth";

    let refreshPromise = null;


    function getAccessToken() {
        return localStorage.getItem(ACCESS_KEY);
    }

    function getRefreshToken() {
        return localStorage.getItem(REFRESH_KEY);
    }

    function saveTokens(tokens) {
        localStorage.setItem(ACCESS_KEY, tokens.accessToken);
        localStorage.setItem(REFRESH_KEY, tokens.refreshToken);
    }

    function clearTokens() {
        localStorage.removeItem(ACCESS_KEY);
        localStorage.removeItem(REFRESH_KEY);
    }

    function isLoggedIn() {
        return Boolean(getAccessToken() && getRefreshToken());
    }


    // JWT состоит из трёх частей, разделённых точкой; вторая часть - JSON с данными (sub, role, exp...)
    function decodePayload(token) {
        try {
            const base64 = token.split(".")[1]
                .replace(/-/g, "+")
                .replace(/_/g, "/");

            const json = decodeURIComponent(
                atob(base64)
                    .split("")
                    .map(function (char) {
                        return "%" + ("00" + char.charCodeAt(0).toString(16)).slice(-2);
                    })
                    .join("")
            );

            return JSON.parse(json);
        } catch (error) {
            return null;
        }
    }

    function getUser() {
        const token = getAccessToken();

        if (!token) {
            return null;
        }

        const payload = decodePayload(token);

        if (!payload) {
            return null;
        }

        return {
            username: payload.sub,
            role: payload.role
        };
    }

    function isAdmin() {
        const user = getUser();

        return Boolean(user && user.role === "ADMIN");
    }


    function redirectToLogin() {
        window.location.replace("login.html");
    }

    function logout() {
        clearTokens();
        redirectToLogin();
    }


    // Бэкенд возвращает ошибки в виде {timestamp, status, error, message, details}
    async function buildError(response, fallbackMessage) {
        let data = null;

        try {
            data = await response.json();
        } catch (ignored) {
            data = null;
        }

        const error = new Error(
            (data && data.message) || fallbackMessage
        );

        error.status = response.status;
        error.details = (data && data.details) || null;

        return error;
    }

    function postJson(path, body) {
        return fetch(AUTH_URL + path, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
    }


    async function login(usernameOrEmail, password) {
        const response = await postJson("/login", {
            login: usernameOrEmail,
            password: password
        });

        if (!response.ok) {
            throw await buildError(response, "Не удалось выполнить вход");
        }

        saveTokens(await response.json());
    }

    async function register(username, email, password) {
        const response = await postJson("/register", {
            username: username,
            email: email,
            password: password
        });

        if (!response.ok) {
            throw await buildError(response, "Не удалось зарегистрироваться");
        }

        return response.json();
    }


    // Refresh-токен одноразовый (ротация), поэтому параллельные запросы ждут один общий refresh
    function refreshTokens() {
        if (refreshPromise) {
            return refreshPromise;
        }

        const refreshToken = getRefreshToken();

        if (!refreshToken) {
            return Promise.resolve(false);
        }

        refreshPromise = (async function () {
            try {
                const response = await postJson("/refresh", {
                    refreshToken: refreshToken
                });

                if (!response.ok) {
                    return false;
                }

                saveTokens(await response.json());
                return true;

            } catch (error) {
                return false;

            } finally {
                refreshPromise = null;
            }
        })();

        return refreshPromise;
    }


    // fetch с заголовком Authorization; при 401 один раз обновляет токены и повторяет запрос
    async function apiFetch(url, options = {}) {
        function send() {
            const headers = Object.assign({}, options.headers || {});
            const token = getAccessToken();

            if (token) {
                headers["Authorization"] = "Bearer " + token;
            }

            return fetch(url, Object.assign({}, options, { headers: headers }));
        }

        let response = await send();

        if (response.status === 401) {
            const refreshed = await refreshTokens();

            if (refreshed) {
                response = await send();
            }

            if (!refreshed || response.status === 401) {
                clearTokens();
                redirectToLogin();
                throw new Error("Сессия истекла, войдите заново");
            }
        }

        return response;
    }


    function renderUserBar() {
        const user = getUser();

        if (!user) {
            return;
        }

        const bar = document.createElement("div");
        bar.classList.add("user-bar");

        const info = document.createElement("span");
        info.textContent = user.username + " (" + user.role + ")";

        const logoutButton = document.createElement("button");
        logoutButton.type = "button";
        logoutButton.textContent = "Выйти";
        logoutButton.addEventListener("click", logout);

        bar.appendChild(info);
        bar.appendChild(logoutButton);

        document.body.insertBefore(bar, document.body.firstChild);
    }


    // Вызывается в начале каждой страницы; false означает, что идёт редирект
    function initPage(options = {}) {
        if (!isLoggedIn()) {
            redirectToLogin();
            return false;
        }

        if (options.adminOnly && !isAdmin()) {
            alert("Эта страница доступна только администратору");
            window.location.replace("index.html");
            return false;
        }

        renderUserBar();
        return true;
    }


    return {
        isLoggedIn: isLoggedIn,
        isAdmin: isAdmin,
        getUser: getUser,
        login: login,
        register: register,
        logout: logout,
        apiFetch: apiFetch,
        buildError: buildError,
        initPage: initPage
    };
})();
