import { instance } from "./api.config.js";

const AuthService = {
    register(username, email, password) {
        return instance.post("/api/register", {username, email, password})
    },

    login(email, password) {
        return instance.post("/api/login", {email, password})
    },

    refreshToken() {
        return instance.get("/api/refresh");
    },

    logout() {
        return instance.post("/api/logout")
    }
};

export default AuthService;
