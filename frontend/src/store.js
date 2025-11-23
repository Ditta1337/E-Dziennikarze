import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useStore = create(
    persist(
        (set) => ({
            user: null,
            token: null,
            refresh: null,
            chatHistory: [],
            setUser: (newUser) => set(() => ({ user: newUser })),
            setToken: (newToken) => set(() => ({ token: newToken })),
            setRefresh: (newRefresh) => set(() => ({ refresh: newRefresh })),
            addToChatHistory: (chatUser) => set((state) => {
                const filtered = state.chatHistory.filter(u => u.id !== chatUser.id);
                return { chatHistory: [chatUser, ...filtered] };
            }),
            logout: () => set(() => ({ user: null, token: null, refresh: null, chatHistory: [] })),
        }),
        {
            name: "storage"
        }
    )
);