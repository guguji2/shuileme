import { reactive } from 'vue'

const state = reactive({
    user: null,
    token: '',
    isLoggedIn: false
})

export function useStore() {
    const setUser = (user) => {
        state.user = user
        state.isLoggedIn = !!user
        uni.setStorageSync('user', user)
    }

    const setToken = (token) => {
        state.token = token
        uni.setStorageSync('token', token)
    }

    const logout = () => {
        state.user = null
        state.token = ''
        state.isLoggedIn = false
        uni.removeStorageSync('user')
        uni.removeStorageSync('token')
    }

    const loadFromStorage = () => {
        const user = uni.getStorageSync('user')
        const token = uni.getStorageSync('token')
        if (user) {
            state.user = user
            state.isLoggedIn = true
        }
        if (token) {
            state.token = token
        }
    }

    return {
        state,
        setUser,
        setToken,
        logout,
        loadFromStorage
    }
}
