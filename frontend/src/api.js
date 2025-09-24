import axios from "axios";
import { useStore } from "./store";

const apiClient = axios.create({
    baseURL: process.env.BACKEND_BASE_URL || "http://localhost:8443",
    headers: {
        "Content-Type": "application/json",
    },
});

apiClient.interceptors.request.use(
    (config) => {
        const token = useStore.getState().token;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        console.log(error)
        if (error.response?.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                return new Promise(function(resolve, reject) {
                    failedQueue.push({ resolve, reject });
                }).then(token => {
                    originalRequest.headers['Authorization'] = 'Bearer ' + token;
                    return apiClient(originalRequest);
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;

            const { refresh, setToken, setRefresh, logout } = useStore.getState();

            if (!refresh) {
                logout();
                return Promise.reject(error);
            }

            try {
                const response = await axios.post(`${apiClient.defaults.baseURL}/auth/refresh`, {
                    refreshToken: refresh,
                });

                const { accessToken, refreshToken } = response.data;

                setToken(accessToken);
                setRefresh(refreshToken);

                apiClient.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
                originalRequest.headers['Authorization'] = 'Bearer ' + accessToken;

                processQueue(null, accessToken);
                return apiClient(originalRequest);
            } catch (refreshError) {
                processQueue(refreshError, null);
                logout();
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        return Promise.reject(error);
    }
);


export const get = (url, params = {}) => apiClient.get(url, { params });
export const post = (url, data) => apiClient.post(url, data);
export const put = (url, data) => apiClient.put(url, data);
export const patch = (url, data) => apiClient.patch(url, data);
export const del = (url) => apiClient.delete(url);