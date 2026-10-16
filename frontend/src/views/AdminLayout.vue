<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { logout as apiLogout } from '../api'

const route = useRoute()
const router = useRouter()

const user = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('campus_user') || 'null')
  } catch {
    return null
  }
})

async function logout() {
  await apiLogout()
  router.push('/login')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink to="/admin" class="admin-brand">
        <span class="brand-mark">拾</span>
        <span>拾光 <em>后台</em></span>
      </RouterLink>
      <p class="admin-nav-label">工作台</p>
      <nav class="admin-nav" aria-label="管理员导航">
        <RouterLink to="/admin" :class="{ active: route.path === '/admin' }">
          <span aria-hidden="true">▦</span> 概览
        </RouterLink>
        <RouterLink to="/admin/notes" :class="{ active: route.path.startsWith('/admin/notes') }">
          <span aria-hidden="true">▤</span> 笔记管理
        </RouterLink>
      </nav>
      <div class="admin-sidebar-bottom">
        <RouterLink to="/" class="admin-back-link">← 返回社区</RouterLink>
        <button class="admin-logout" @click="logout">退出登录</button>
      </div>
    </aside>

    <section class="admin-content">
      <header class="admin-topbar">
        <div>
          <p class="admin-kicker">CAMPUS ADMIN</p>
          <h1>管理员工作台</h1>
        </div>
        <div class="admin-account">
          <span class="admin-avatar">admin</span>
          <span>{{ user?.nickname || user?.username || '管理员' }}</span>
        </div>
      </header>
      <main class="admin-main"><RouterView /></main>
    </section>
  </div>
</template>
