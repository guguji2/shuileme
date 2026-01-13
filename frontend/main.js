import App from './App.vue'

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
    const app = createSSRApp(App)
    return {
        app
    }
}
// #endif

// #ifndef VUE3
import Vue from 'vue'
export default new Vue({
    ...App
})
// #endif
