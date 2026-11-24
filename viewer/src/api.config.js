import axios from "axios";

export const instance = axios.create({
  withCredentials: true,
  baseURL: "http://localhost:80/"
});


instance.interceptors.response.use(
  (config) => {
    return config;
  },
  async (error) => {
   const originalRequest = {...error.config};
   originalRequest._isRetry = true; 
    if (
      error.response.status === 401 && 
      error.config &&
      !error.config._isRetry
    ) {
      try {
        const resp = await instance.get("/api/refresh");
        return instance.request(originalRequest);
      } catch (error) {
        console.log("AUTH ERROR");
      }
    }
    throw error;
  }
);
