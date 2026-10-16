<script setup>
import { computed, nextTick } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
const props = defineProps({ loggedIn: Boolean, user: { type: Object, default: null } })
const emit = defineEmits(['logout'])
const router = useRouter()
const route = useRoute()
const user = computed(() => props.user)

async function goToSection(sectionId) {
  if (route.path !== '/') await router.push('/')
  await nextTick()
  document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

async function goToHome() {
  if (route.path !== '/') await router.push('/')
  await nextTick()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>
<template>
  <header class="app-header">
    <div class="header-inner">
      <RouterLink to="/" class="brand"><span class="brand-mark">拾</span><span>拾光 <em>Campus</em></span></RouterLink>
      <nav class="desktop-nav"><a href="#top" @click.prevent="goToHome">发现</a><a href="#featured" @click.prevent="goToSection('featured')">校园灵感</a><a href="#about" @click.prevent="goToSection('about')">关于拾光</a></nav>
      <div class="header-actions">
        <button class="icon-button" aria-label="搜索" @click="router.push('/')">⌕</button>
        <RouterLink v-if="props.loggedIn && String(user?.role || '').toLowerCase() === 'admin'" to="/admin" class="saved-link admin-entry">后台管理</RouterLink>
        <RouterLink v-if="!props.loggedIn" to="/login" class="text-button">登录</RouterLink>
        <RouterLink v-if="!props.loggedIn" to="/register" class="dark-button small">加入拾光</RouterLink>
        <div v-else class="user-menu"><RouterLink to="/profile"><img v-if="user?.avatar" :src="user.avatar" alt="头像" /><span v-else class="header-avatar-placeholder" aria-hidden="true">{{ (user?.nickname || '校').slice(0, 1) }}</span><span>{{ user?.nickname }}</span></RouterLink><button class="logout" @click="emit('logout')">退出</button></div>
      </div>
    </div>
  </header>
</template>
