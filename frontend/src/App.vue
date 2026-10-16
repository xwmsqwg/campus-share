<script setup>
import { ref, watch } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import AppHeader from './components/AppHeader.vue'
import { DEMO_API_STATUS, PENDING_API_STATUS } from './api/status'
import { apiMode, logout as apiLogout } from './api'
import { seedStorage } from './mock/data'

const router = useRouter()
const route = useRoute()
const loggedIn = ref(Boolean(localStorage.getItem('campus_token')))
const currentUser = ref(readStoredUser())
if (apiMode === 'demo') seedStorage()
const apiStatus = ref(apiMode === 'demo' ? DEMO_API_STATUS : PENDING_API_STATUS)
watch(() => route.fullPath, () => { loggedIn.value = Boolean(localStorage.getItem('campus_token')) })
watch(loggedIn, (value) => { currentUser.value = value ? readStoredUser() : null })
window.addEventListener('campus:api-status', (event) => { apiStatus.value = event.detail })
window.addEventListener('campus:user-updated', (event) => { currentUser.value = event.detail })
async function logout() {
  await apiLogout()
  loggedIn.value = false
  currentUser.value = null
  router.push('/')
}
function readStoredUser() {
  try { return JSON.parse(localStorage.getItem('campus_user') || 'null') } catch { return null }
}
</script>

<template>
  <AppHeader v-if="!route.meta.admin" :logged-in="loggedIn" :user="currentUser" @logout="logout" />
  <div v-if="!route.meta.admin" class="api-status" :class="`api-status-${apiStatus.kind}`">{{ apiStatus.message }}</div>
  <main class="app-main"><RouterView /></main>
</template>
