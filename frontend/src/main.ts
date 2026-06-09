import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { setupApp } from 'wujie'
import WujieVue from 'wujie-vue3'
import './style.css'
import App from './App.vue'
import router from './router'

// 配置 wujie 微前端子应用
const getChildAppUrl = () => {
  if (import.meta.env.VITE_CHILD_APP_URL) {
    return import.meta.env.VITE_CHILD_APP_URL
  }
  return import.meta.env.MODE === 'development' ? 'http://localhost:3000' : './workflow-editor'
}

setupApp({
  name: 'workflow-editor',
  url: getChildAppUrl(),
  alive: true,
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(WujieVue)
app.mount('#app')
