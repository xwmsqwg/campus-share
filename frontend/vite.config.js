import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    host: 'localhost',
    proxy: {
      '/campus-api': 'http://localhost:8080'
    }
  }
})
