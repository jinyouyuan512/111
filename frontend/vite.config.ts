import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia']
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3001,
    proxy: {
      '/webhook': {
        target: 'http://localhost:5678',
        changeOrigin: true,
        timeout: 0,  // 禁用超时，让 axios 控制
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            // 设置代理请求超时为 5 分钟
            proxyReq.setTimeout(300000);
          });
          proxy.on('error', (err, req, res) => {
            console.log('[Proxy Error]', err.message);
          });
        }
      },
      '/api/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/api/users': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/api/admin/users': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/api/admin/content': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/admin/orders': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/api/admin/products': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/api/academy': {
        target: 'http://localhost:8088',
        changeOrigin: true
      },
      '/api/mall': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/api/creative': {
        target: 'http://localhost:8087',
        changeOrigin: true
      },
      '/api/tourism': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/api/posts': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/topics': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/comments': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/upload': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
