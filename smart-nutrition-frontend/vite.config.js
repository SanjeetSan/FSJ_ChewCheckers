import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        secure: false
      }
    }
  }
});
