import { useState, useEffect, useRef, useCallback } from 'react';
import { useStore } from '../store';
import { apiClient } from "../api";

const useWebSocket = (path) => {
    const [isConnected, setIsConnected] = useState(false);
    const socketRef = useRef(null);
    const listenersRef = useRef({});
    const token = useStore((state) => state.token);

    const _emit = (eventName, data) => {
        (listenersRef.current[eventName] || []).forEach(callback => callback(data));
    };

    useEffect(() => {
        if (!token || !path) {
            console.warn("WebSocket: No token or path, not connecting.");
            return;
        }

        const wsProtocol = apiClient.defaults.baseURL.startsWith('https://') ? 'wss' : 'ws';
        const wsBaseURL = apiClient.defaults.baseURL.replace(/^https?/, wsProtocol);
        const wsURL = `${wsBaseURL}${path}?token=${token}`;

        const socket = new WebSocket(wsURL);
        socketRef.current = socket;

        socket.onopen = () => {
            setIsConnected(true);
            _emit('open');
        };

        socket.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                _emit('message', data);
            } catch (e) {
                _emit('message', event.data);
            }
        };

        socket.onclose = () => {
            setIsConnected(false);
            _emit('close');
        };

        socket.onerror = (error) => {
            console.error("WebSocket Error:", error);
            _emit('error', error);
        };

        return () => {
            if (socketRef.current) {
                socketRef.current.onopen = null;
                socketRef.current.onmessage = null;
                socketRef.current.onclose = null;
                socketRef.current.onerror = null;
                socketRef.current.close();
                socketRef.current = null;
            }
            listenersRef.current = {};
        };
    }, [path, token]);

    const on = useCallback((eventName, callback) => {
        if (!listenersRef.current[eventName]) {
            listenersRef.current[eventName] = [];
        }
        listenersRef.current[eventName].push(callback);
    }, []);

    const off = useCallback((eventName, callback) => {
        if (listenersRef.current[eventName]) {
            listenersRef.current[eventName] = listenersRef.current[eventName].filter(
                (cb) => cb !== callback
            );
        }
    }, []);

    const send = useCallback((data) => {
        if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify(data));
        } else {
            console.error("WebSocket: Not connected, cannot send message.");
        }
    }, []);

    return { isConnected, send, on, off };
};

export default useWebSocket;