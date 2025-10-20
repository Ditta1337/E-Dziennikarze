import axios from "axios";
import { useStore } from "./store"; // Assuming your Zustand store is in './store'

const apiUrl = "http://localhost:8443"

const apiClient = axios.create({
    baseURL: process.env.BACKEND_BASE_URL || apiUrl,
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

export const websocketClient = {
    socket: null,
    listeners: {},

    connect(path) {
        if (this.socket) {
            this.disconnect();
        }

        const token = useStore.getState().token;

        if (!token || !path) {
            console.error("WebSocket connection failed: No token or path provided.");
            return;
        }

        const wsProtocol = apiClient.defaults.baseURL.startsWith('https://') ? 'wss' : 'ws';
        const wsBaseURL = apiClient.defaults.baseURL.replace(/^https?/, wsProtocol);
        const wsURL = `${wsBaseURL}${path}?token=${token}`;

        this.socket = new WebSocket(wsURL);

        this.socket.onopen = () => this._emit('open');
        this.socket.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                this._emit('message', data);
            } catch (e) {
                this._emit('message', event.data);
            }
        };
        this.socket.onclose = () => {
            if (this.socket) {
                this.socket = null;
                this._emit('close');
            }
        };
        this.socket.onerror = (error) => this._emit('error', error);
    },

    disconnect() {
        if (this.socket) {
            this.socket.onopen = null;
            this.socket.onmessage = null;
            this.socket.onerror = null;
            this.socket.onclose = null;
            this.socket.close();
            this.socket = null;
        }
    },

    send(data) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(data));
        }
    },

    on(eventName, callback) {
        if (!this.listeners[eventName]) {
            this.listeners[eventName] = [];
        }
        this.listeners[eventName].push(callback);
    },

    off(eventName, callback) {
        if (this.listeners[eventName]) {
            this.listeners[eventName] = this.listeners[eventName].filter(
                (cb) => cb !== callback
            );
        }
    },

    _emit(eventName, data) {
        (this.listeners[eventName] || []).forEach(callback => callback(data));
    }
};

export const get = (url, params = {}) => apiClient.get(url, { params });
export const post = (url, data, config = {}) => apiClient.post(url, data, config);
export const put = (url, data) => apiClient.put(url, data);
export const patch = (url, data) => apiClient.patch(url, data);
export const del = (url) => apiClient.delete(url);