import axios from "axios"

const apiClient = axios.create({
    baseURL: process.env.BACKEND_BASE_URL || "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
    timeout: 10000,
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error("API Error:", error);
        return Promise.reject(error);
    }
);

export const get = (url, params = {}) => apiClient.get(url, { params });
export const post = (url, data) => apiClient.post(url, data);
export const put = (url, data) => apiClient.put(url, data);
export const patch = (url, data) => apiClient.patch(url, data);
export const del = (url) => apiClient.delete(url);
