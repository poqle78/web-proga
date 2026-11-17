
const Auth = (function () {
    const ACCESS_KEY = "accessToken";
    const REFRESH_KEY = "refreshToken";
    const AUTH_URL = "api/v1/auth";

    const COOKIE_PATH = "/";
    const COOKIE_SAME_SITE = "Lax";

    let refreshPromise = null;

    function getCookie(name) {
        const prefix = encodeURIComponent(name) + "=";
        const cookie = document.cookie
            .split("; ")
            .find(item => item.startsWith(prefix));

        return cookie
            ? decodeURIComponent(cookie.substring(prefix.length))
            : null;
    }

    function getTokenMaxAge(token, fallback) {
        const payload = decodePayload(token);

        if (payload && payload.exp) {
            return Math.max(0, payload.exp - Math.floor(Date.now() / 1000));
        }

        return fallback;
    }

    function setCookie(name, value, maxAge) {
        let cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}`;
        cookie += `; Path=${COOKIE_PATH}`;
        cookie += `; Max-Age=${maxAge}`;
        cookie += `; SameSite=${COOKIE_SAME_SITE}`;

        if (location.protocol === "https:") {
            cookie += "; Secure";
        }

        document.cookie = cookie;
    }

    function getAccessToken() {
        return getCookie(ACCESS_KEY);
    }

    function getRefreshToken() {
        return getCookie(REFRESH_KEY);
    }

    function saveTokens(tokens) {
        if (!tokens || !tokens.accessToken || !tokens.refreshToken) {
            throw new Error("Сервер вернул некорректные токены");
        }

        const accessMaxAge = getTokenMaxAge(tokens.accessToken, 900);
        const refreshMaxAge = getTokenMaxAge(tokens.refreshToken, 2592000);

        if (accessMaxAge <= 0 || refreshMaxAge <= 0) {
            throw new Error("Получены просроченные токены");
        }

        setCookie(ACCESS_KEY, tokens.accessToken, accessMaxAge);
        setCookie(REFRESH_KEY, tokens.refreshToken, refreshMaxAge);
    }

    function clearTokens() {
        setCookie(ACCESS_KEY, "", 0);
        setCookie(REFRESH_KEY, "", 0);
    }

    function isLoggedIn() {
        return Boolean(getAccessToken() && getRefreshToken());
    }

    function decodePayload(token) {
        try {
            const base64 = token.split(".")[1]
                .replace(/-/g, "+")
                .replace(/_/g, "/");

            const normalized = base64.padEnd(
                Math.ceil(base64.length / 4) * 4,
                "="
            );

            const json = decodeURIComponent(
                atob(normalized)
                    .split("")
                    .map(char =>
                        "%" + ("00" + char.charCodeAt(0).toString(16)).slice(-2)
                    )
                    .join("")
            );

            return JSON.parse(json);
        } catch (error) {
            return null;
        }
    }

    function getUser() {
        const token = getAccessToken();

        if (!token) return null;

        const payload = decodePayload(token);

        if (!payload) return null;

        return {
            username: payload.sub,
            role: payload.roles
        };
    }

    function isAdmin() {
        const user = getUser();
        return Boolean(user && user.role === "ADMIN");
    }

    function redirectToLogin() {
        window.location.replace("registration.html");
    }

    function logout() {
        clearTokens();
        redirectToLogin();
    }

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
            username,
            email,
            password
        });

        if (!response.ok) {
            throw await buildError(response, "Не удалось зарегистрироваться");
        }

        return response.json();
    }

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
                    refreshToken
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

    async function apiFetch(url, options = {}) {
        async function send() {
            const headers = new Headers(options.headers || {});
            const token = getAccessToken();

            if (token) {
                headers.set("Authorization", "Bearer " + token);
            }

            return fetch(url, {
                ...options,
                headers
            });
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

        if (!user) return;

        const bar = document.createElement("div");
        bar.classList.add("user-bar");

        const info = document.createElement("span");
        info.textContent = `${user.username} (${user.role})`;

        const logoutButton = document.createElement("button");
        logoutButton.type = "button";
        logoutButton.textContent = "Выйти";
        logoutButton.addEventListener("click", logout);

        bar.appendChild(info);
        bar.appendChild(logoutButton);

        document.body.insertBefore(bar, document.body.firstChild);
    }

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
        isLoggedIn,
        isAdmin,
        getUser,
        login,
        register,
        logout,
        apiFetch,
        buildError,
        initPage
    };
})();
