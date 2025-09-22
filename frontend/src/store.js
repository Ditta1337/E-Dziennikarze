import {create} from 'zustand';
import {persist} from 'zustand/middleware';

export const useStore = create(
    persist(
        (set) => ({
            user: null,
            token: null,
            refresh: null,
            setUser: (newUser) => set(() => ({ user: newUser })),
            setToken: (newToken) => set(() => ({ token: newToken })),
            setRefresh: (newRefresh) => set(() => ({ refresh: newRefresh })),
            logout: () => set(() => ({ user: null, token: null, refresh: null })),


            test: 0,
            role: "admin",
            setRole: (newRole) => set(() => ({ role: newRole })),
            increaseTest: () => set((state) => ({test: state.test + 1})),
            decreaseTest: () => set((state) => ({test: state.test - 1})),
        }),
        {
            name: "storage"
        }
    )
);