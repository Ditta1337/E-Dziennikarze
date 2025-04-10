import {create} from 'zustand';
import {persist} from 'zustand/middleware';

export const useStore = create(
    persist(
        (set) => ({
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